package com.gfbdev.session;

import com.gfbdev.Messages;
import com.gfbdev.entity.*;
import com.gfbdev.entity.dto.DateFilter;
import com.gfbdev.repository.CustomerRepository;
import com.gfbdev.repository.DeliveryRepository;
import com.gfbdev.repository.ProviderRepository;
import com.gfbdev.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Headtrap on 28/08/2017.
 */
@Component
public class TransactionSession {

    private final
    CustomerSession customerSession;

    private final
    ConsumptionSession consumptionSession;

    private final
    ProviderSession providerSession;

    private final
    LobbySession lobbySession;

    private final
    CustomerRepository customerRepository;

    private final
    ProviderRepository providerRepository;

    private final
    TransactionRepository transactionRepository;

    private final
    DeliveryRepository deliveryRepository;

    @Autowired
    public TransactionSession(CustomerSession customerSession, ConsumptionSession consumptionSession, ProviderSession providerSession, LobbySession lobbySession, CustomerRepository customerRepository, ProviderRepository providerRepository, TransactionRepository transactionRepository, DeliveryRepository deliveryRepository) {
        this.customerSession = customerSession;
        this.consumptionSession = consumptionSession;
        this.providerSession = providerSession;
        this.lobbySession = lobbySession;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.transactionRepository = transactionRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public Response add(Transaction transaction) {
        try {
            Response responseCustomer = customerSession.findCustomer(transaction.getCustomerId());
            if (!responseCustomer.status) {
                return responseCustomer;
            }

            Response responseProvider = providerSession.findProvider(transaction.getProviderId());
            if (!responseProvider.status) {
                return responseProvider;
            }

            Customer customer = (Customer) responseCustomer.getData();
            Provider provider = (Provider) responseProvider.getData();
            transaction.setDate(new Date());
            Transaction savedTransaction = transactionRepository.save(transaction);

            customer.getPurchases().add(savedTransaction);
            provider.getSales().add(savedTransaction);

            customerRepository.save(customer);
            providerRepository.save(provider);

            Response responseConsumption = consumptionSession.removeConsumption(customer.getId(), provider.getId());
            if (!responseConsumption.status) {
                return responseConsumption;
            }

            Response responseCheckout = lobbySession.checkOut(customer.getId(), provider.getId());
            if (!responseCheckout.status) {
                return responseCheckout;
            }

            return Response.ok(Messages.getInstance().getString("messages.success.transaction-saved"));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response findTransaction(String transactionId) {
        try {
            Transaction transaction = transactionRepository.findOne(transactionId);
            if (transaction == null) {
                return Response.ok("Transação não encontrada");
            }

            return Response.ok((transaction));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response geTransactions(String providerId) {
        try {
            List<Transaction> transactions = transactionRepository.findByProviderIdOrderByDateDesc(providerId);
            if (transactions == null) {
                return Response.error("Nenhuma transação encontrada");
            }
            return Response.ok(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response addDelivery(Delivery delivery) {
        try {
            Response responseCustomer = customerSession.findCustomer(delivery.getCustomerId());
            if (!responseCustomer.status) {
                return responseCustomer;
            }

            Response responseProvider = providerSession.findProvider(delivery.getProviderId());
            if (!responseProvider.status) {
                return responseProvider;
            }

            Customer customer = (Customer) responseCustomer.getData();
            Provider provider = (Provider) responseProvider.getData();
            delivery.setDate(new Date());
            Transaction savedTransaction = transactionRepository.save(delivery);

            customer.getPurchases().add(savedTransaction);
            provider.getSales().add(savedTransaction);

            customerRepository.save(customer);
            providerRepository.save(provider);

            return Response.ok(Messages.getInstance().getString("messages.success.delivery-saved"));

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response updateDelivery(Delivery delivery) {
        try {
            deliveryRepository.save(delivery);
            return Response.ok(Messages.getInstance().getString("messages.success.items-delivered"));
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response getCustomerTransactions(String customerId) {
        try {
            List<Transaction> transactions = transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
            if (transactions == null) {
                return Response.error("Nenhuma transação encontrada");
            }
            return Response.ok(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response filterSales(String providerId, DateFilter dateFilter) {
        try {
            List<Transaction> transactions = transactionRepository
                    .findByDateBetweenAndProviderIdOrderByDateDesc(dateFilter.startDate, dateFilter.endDate, providerId);

            return Response.ok(transactions);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response filterPurchases(String userId, DateFilter dateFilter) {
        try {
            List<Transaction> transactions = transactionRepository
                    .findByDateBetweenAndCustomerIdOrderByDateDesc(dateFilter.startDate, dateFilter.endDate, userId);

            return Response.ok(transactions);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
}


