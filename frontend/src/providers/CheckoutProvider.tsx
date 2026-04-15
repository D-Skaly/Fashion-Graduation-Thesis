"use client";

import React, { createContext, useContext, useState, useCallback } from "react";

export enum PaymentMethod {
  COD = "COD",
  VNPAY = "VNPAY",
  MOMO = "MOMO",
}

export interface ShippingInfo {
  fullName: string;
  phone: string;
  address: string;
  note?: string;
}

export interface CheckoutState {
  shipping: ShippingInfo;
  paymentMethod: PaymentMethod;
  discountCode?: string;
  discountAmount: number;
  isProcessing: boolean;
}

interface CheckoutContextType {
  state: CheckoutState;
  setShipping: (shipping: ShippingInfo) => void;
  setPaymentMethod: (method: PaymentMethod) => void;
  setDiscountCode: (code: string | undefined, amount?: number) => void;
  setIsProcessing: (isProcessing: boolean) => void;
  resetCheckout: () => void;
}

const defaultState: CheckoutState = {
  shipping: {
    fullName: "",
    phone: "",
    address: "",
    note: "",
  },
  paymentMethod: PaymentMethod.COD,
  discountAmount: 0,
  isProcessing: false,
};

const CheckoutContext = createContext<CheckoutContextType | undefined>(undefined);

export function CheckoutProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<CheckoutState>(defaultState);

  const setShipping = useCallback((shipping: ShippingInfo) => {
    setState((prev) => ({ ...prev, shipping }));
  }, []);

  const setPaymentMethod = useCallback((paymentMethod: PaymentMethod) => {
    setState((prev) => ({ ...prev, paymentMethod }));
  }, []);

  const setDiscountCode = useCallback((discountCode: string | undefined, discountAmount = 0) => {
    setState((prev) => ({ ...prev, discountCode, discountAmount }));
  }, []);

  const setIsProcessing = useCallback((isProcessing: boolean) => {
    setState((prev) => ({ ...prev, isProcessing }));
  }, []);

  const resetCheckout = useCallback(() => {
    setState(defaultState);
  }, []);

  return (
    <CheckoutContext.Provider
      value={{
        state,
        setShipping,
        setPaymentMethod,
        setDiscountCode,
        setIsProcessing,
        resetCheckout,
      }}
    >
      {children}
    </CheckoutContext.Provider>
  );
}

export function useCheckout() {
  const context = useContext(CheckoutContext);
  if (context === undefined) {
    throw new Error("useCheckout must be used within a CheckoutProvider");
  }
  return context;
}
