package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.entity.Plan;
import com.abhinay.buildrix_ai.entity.User;

public interface PaymentProcessor {

     String  openCustomerPortal(String stripeCustomerId);

     String checkout(Plan plan, User user);

}
