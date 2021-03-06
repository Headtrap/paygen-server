package com.gfbdev.session;

import com.gfbdev.Messages;
import com.gfbdev.entity.*;
import com.gfbdev.repository.ProductRepository;
import com.gfbdev.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConsumptionSession {

    private final
    CustomerSession customerSession;

    private final
    ProviderSession providerSession;

    private final
    ProviderRepository providerRepository;

    private final
    ProductSession productSession;

    private final
    ProductRepository productRepository;


    @Autowired
    public ConsumptionSession(CustomerSession customerSession, ProviderSession providerSession,
                              ProviderRepository providerRepository, ProductSession productSession, ProductRepository productRepository) {
        this.customerSession = customerSession;
        this.providerSession = providerSession;
        this.providerRepository = providerRepository;
        this.productSession = productSession;
        this.productRepository = productRepository;
    }


    public Response addConsumption(Customer customer, Provider provider) {
        try {
            Consumption consumption = new Consumption();
            consumption.setItems(new ArrayList<>());
            consumption.setCustomer(customer);
            provider.getConsumptions().add(consumption);
            return Response.ok(providerRepository.save(provider));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response getConsumption(String customerId, String providerId) {
        try {
            Response responseCustomer = customerSession.findCustomer(customerId);
            if (!responseCustomer.status) {
                return responseCustomer;
            }
            Response responseProvider = providerSession.findProvider(providerId);
            if (!responseProvider.status) {
                return responseProvider;
            }

            Provider provider = (Provider) responseProvider.getData();
            Customer customer = (Customer) responseCustomer.getData();
            for (Consumption c : provider.getConsumptions()) {
                if (c.getCustomer().equals(customer)) {
                    return Response.ok(c);
                }
            }
            return Response.error(Messages.getInstance().getString("messages.error.consumption-not-found"));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response addItem(String providerId, String customerId, String itemId) {
        try {
            Response responseCustomer = customerSession.findCustomer(customerId);
            if (!responseCustomer.status) {
                return responseCustomer;
            }
            Response responseProvider = providerSession.findProvider(providerId);
            if (!responseProvider.status) {
                return responseProvider;
            }

            Response reponseItem = productSession.findProduct(itemId);
            if (!reponseItem.status) {
                return responseProvider;
            }

            Response responseConsumption = getConsumption(customerId, providerId);
            if (!responseConsumption.status) {
                return responseConsumption;
            }
            Provider provider = (Provider) responseProvider.data;
            Consumption consumption = (Consumption) responseConsumption.getData();
            Product product = (Product) reponseItem.data;
            product.setAmount(product.getAmount() + 1);

            provider.getConsumptions().get(provider.getConsumptions().indexOf(consumption))
                    .getItems()
                    .add(product);

            productRepository.save(product);
            providerRepository.save(provider);
            return Response.ok(Messages.getInstance().getString("messages.info.item-added"));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response removeItem(String providerId, String customerId, String itemId) {
        try {
            Response responseCustomer = customerSession.findCustomer(customerId);
            if (!responseCustomer.status) {
                return responseCustomer;
            }
            Response responseProvider = providerSession.findProvider(providerId);
            if (!responseProvider.status) {
                return responseProvider;
            }

            Response responseConsumption = getConsumption(customerId, providerId);
            if (!responseConsumption.status) {
                return responseConsumption;
            }

            Response responseItem = productSession.findProduct(itemId);
            if (!responseItem.status) {
                return responseItem;
            }

            Provider provider = (Provider) responseProvider.data;
            Consumption consumption = (Consumption) responseConsumption.getData();
            Product product = (Product) responseItem.data;
            product.setAmount(product.getAmount() - 1);

            provider.getConsumptions().get(provider.getConsumptions().indexOf(consumption))
                    .getItems()
                    .remove(product);

            productRepository.save(product);
            providerRepository.save(provider);

            return Response.ok(Messages.getInstance().getString("messages.info.item-removed"));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Response removeConsumption(String customerId, String providerId) {
        try {
            Response responseConsumption = getConsumption(customerId, providerId);
            if (!responseConsumption.status) {
                return responseConsumption;
            }

            Provider provider = providerRepository.findOne(providerId);
            Consumption consumption = (Consumption) responseConsumption.data;
            List<Consumption> consumptions = provider.getConsumptions();
            consumptions.get(consumptions.indexOf(consumption)).getItems().clear();

            providerRepository.save(provider);
            return Response.ok(Messages.getInstance().getString("messages.success.consumption.removed"));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
}