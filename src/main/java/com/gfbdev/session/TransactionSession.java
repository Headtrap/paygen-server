package com.gfbdev.session;

import com.gfbdev.Messages;
import com.gfbdev.entity.*;
import com.gfbdev.repository.CustomerRepository;
import com.gfbdev.repository.ProviderRepository;
import com.gfbdev.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    CustomerRepository customerRepository;

    private final
    ProviderRepository providerRepository;

    private final
    TransactionRepository transactionRepository;

    @Autowired
    public TransactionSession(CustomerSession customerSession, ConsumptionSession consumptionSession, ProviderSession providerSession, CustomerRepository customerRepository, ProviderRepository providerRepository, TransactionRepository transactionRepository) {
        this.customerSession = customerSession;
        this.consumptionSession = consumptionSession;
        this.providerSession = providerSession;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.transactionRepository = transactionRepository;
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

            transactionRepository.save(transaction);

            customer.getPurchases().add(transaction);
            provider.getSales().add(transaction);

            customerRepository.save(customer);
            providerRepository.save(provider);

            Response responseConsumption = consumptionSession.removeConsumption(customer.getId(), provider.getId());
            if (!responseConsumption.status) {
                return responseConsumption;
            }

            return Response.ok(Messages.getInstance().getString("messages.success.transaction-saved"));

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}


