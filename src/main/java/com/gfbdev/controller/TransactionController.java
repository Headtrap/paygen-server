package com.gfbdev.controller;

import com.gfbdev.entity.Delivery;
import com.gfbdev.entity.Response;
import com.gfbdev.entity.Transaction;
import com.gfbdev.entity.dto.DateFilter;
import com.gfbdev.session.TransactionSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Headtrap on 28/08/2017.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final
    TransactionSession session;

    @Autowired
    public TransactionController(TransactionSession session) {
        this.session = session;
    }

    @RequestMapping("/addTransaction")
    public Response add(@RequestBody Transaction transaction) {
        return session.add(transaction);
    }

    @RequestMapping("/getTransactions/{providerId}")
    public Response getTransactions(@PathVariable("providerId") String providerId) {
        return session.geTransactions(providerId);
    }

    @RequestMapping("/getCustomerTransactions/{customerId}")
    public Response getCustomerTransactions(@PathVariable("customerId") String providerId) {
        return session.getCustomerTransactions(providerId);
    }

    @RequestMapping("/findTransaction/{transactionId}")
    public Response findTransaction(@PathVariable("transactionId") String transactionId) {
        return session.findTransaction(transactionId);
    }

    @RequestMapping("/filterSales/{providerId}")
    public Response filterSales(@PathVariable("providerId") String providerId,
                                      @RequestBody DateFilter dateFilter) {
        return session.filterSales(providerId, dateFilter);
    }

    @RequestMapping("/filterPurchases/{userId}")
    public Response filterPurchases(@PathVariable("userId") String providerId,
                                      @RequestBody DateFilter dateFilter) {
        return session.filterPurchases(providerId, dateFilter);
    }


    @RequestMapping("/addDelivery")
    public Response addDelivery(@RequestBody Delivery delivery) {
        return session.addDelivery(delivery);
    }


    @RequestMapping("/updateDelivery")
    public Response updateDelivery(@RequestBody Delivery delivery) {
        return session.updateDelivery(delivery);
    }

}
