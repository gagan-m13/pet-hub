package com.pethub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod = "CASH_ON_DELIVERY";

    public OrderRequest() {
    }

    public OrderRequest(Long shippingAddressId, String paymentMethod) {
        this.shippingAddressId = shippingAddressId;
        this.paymentMethod = paymentMethod;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
