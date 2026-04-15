"use client";

import { useFormContext } from "react-hook-form";
import { CreditCard, Truck, Wallet, QrCode } from "lucide-react";

import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
} from "@/components/ui/form";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { cn } from "@/lib/utils";

export enum PaymentMethod {
  COD = "COD",
  VNPAY = "VNPAY",
  MOMO = "MOMO",
}

interface PaymentMethodSelectorProps {
  disabled?: boolean;
}

const paymentMethods = [
  {
    id: PaymentMethod.COD,
    label: "Cash on Delivery",
    description: "Pay when you receive your order",
    icon: Truck,
  },
  {
    id: PaymentMethod.VNPAY,
    label: "VNPay",
    description: "Pay with ATM card / QR code",
    icon: CreditCard,
  },
  {
    id: PaymentMethod.MOMO,
    label: "MoMo Wallet",
    description: "Pay with MoMo e-wallet",
    icon: Wallet,
  },
];

export function PaymentMethodSelector({ disabled = false }: PaymentMethodSelectorProps) {
  const { control } = useFormContext();

  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold flex items-center gap-2">
        <CreditCard className="h-5 w-5" />
        Payment Method
      </h3>

      <FormField
        control={control}
        name="paymentMethod"
        render={({ field }) => (
          <FormItem>
            <FormControl>
              <RadioGroup
                onValueChange={field.onChange}
                defaultValue={field.value}
                className="grid grid-cols-1 gap-3"
                disabled={disabled}
              >
                {paymentMethods.map((method) => {
                  const Icon = method.icon;
                  const isSelected = field.value === method.id;

                  return (
                    <FormItem key={method.id}>
                      <FormLabel
                        className={cn(
                          "flex items-center gap-4 p-4 border rounded-lg cursor-pointer transition-all",
                          isSelected 
                            ? "border-primary bg-primary/5" 
                            : "border-border hover:border-primary/50",
                          disabled && "opacity-50 cursor-not-allowed"
                        )}
                      >
                        <FormControl>
                          <RadioGroupItem value={method.id} disabled={disabled} />
                        </FormControl>
                        <div className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center">
                          <Icon className="h-5 w-5 text-primary" />
                        </div>
                        <div className="flex-1">
                          <p className="font-medium">{method.label}</p>
                          <p className="text-sm text-muted-foreground">
                            {method.description}
                          </p>
                        </div>
                        {method.id === PaymentMethod.VNPAY && (
                          <span className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded">
                            Popular
                          </span>
                        )}
                      </FormLabel>
                    </FormItem>
                  );
                })}
              </RadioGroup>
            </FormControl>
          </FormItem>
        )}
      />
    </div>
  );
}
