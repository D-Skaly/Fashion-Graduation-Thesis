"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useRouter } from "next/navigation";
import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { Loader2, ArrowLeft, AlertCircle } from "lucide-react";
import Link from "next/link";
import { toast } from "sonner";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import { Form } from "@/components/ui/form";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Separator } from "@/components/ui/separator";

import { ShippingForm } from "@/components/checkout/ShippingForm";
import { PaymentMethodSelector, PaymentMethod } from "@/components/checkout/PaymentMethodSelector";
import { OrderSummary } from "@/components/checkout/OrderSummary";

// Cart types
interface CartItem {
  id: string;
  productVariantId: string;
  productName: string;
  size: string;
  color: string;
  price: number;
  quantity: number;
  subtotal: number;
}

interface Cart {
  id: string;
  items: CartItem[];
  totalAmount: number;
}

// Validation Schema
const checkoutSchema = z.object({
  fullName: z.string().min(2, "Full Name is required"),
  phone: z.string().min(10, "Phone number must be at least 10 digits"),
  address: z.string().min(10, "Address must be at least 10 characters"),
  note: z.string().optional(),
  paymentMethod: z.nativeEnum(PaymentMethod).default(PaymentMethod.COD),
});

type CheckoutFormValues = z.infer<typeof checkoutSchema>;

const fetchCart = async (): Promise<Cart> => {
  const { data } = await api.get("/cart");
  return data;
};

export default function CheckoutPage() {
  const router = useRouter();
  const queryClient = useQueryClient();

  // Fetch cart data
  const { data: cart, isLoading: isCartLoading } = useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart,
  });

  // Setup Form
  const form = useForm<CheckoutFormValues>({
    resolver: zodResolver(checkoutSchema),
    defaultValues: {
      fullName: "",
      phone: "",
      address: "",
      note: "",
      paymentMethod: PaymentMethod.COD,
    },
  });

  // Setup Order Mutation
  const orderMutation = useMutation({
    mutationFn: async (values: CheckoutFormValues) => {
      const shippingAddress = `${values.fullName}, ${values.phone}, ${values.address}${values.note ? ` (Note: ${values.note})` : ""}`;
      
      const payload = {
        shippingAddress,
        paymentMethod: values.paymentMethod,
      };
      
      const response = await api.post("/orders", payload);
      return response.data;
    },
    onSuccess: (data) => {
      toast.success("Order placed successfully!");
      queryClient.invalidateQueries({ queryKey: ["cart"] });
      
      // Handle different payment methods
      const paymentMethod = form.getValues("paymentMethod");
      if (paymentMethod === PaymentMethod.VNPAY || paymentMethod === PaymentMethod.MOMO) {
        // Redirect to payment gateway
        const orderId = data.data?.id;
        router.push(`/payment/callback?orderId=${orderId}&method=${paymentMethod}`);
      } else {
        // COD - go to success page
        router.push("/checkout/success");
      }
    },
    onError: (error: any) => {
      console.error("Order failed:", error);
      toast.error(error.response?.data?.message || "Failed to place order. Please try again.");
    }
  });

  function onSubmit(values: CheckoutFormValues) {
    orderMutation.mutate(values);
  }

  // Redirect to cart if cart is empty
  if (!isCartLoading && (!cart || cart.items.length === 0)) {
    return (
      <div className="container mx-auto px-4 py-16 max-w-2xl">
        <Alert>
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            Your cart is empty. Please add items before checkout.
          </AlertDescription>
        </Alert>
        <div className="mt-6 text-center">
          <Button asChild>
            <Link href="/shop">Continue Shopping</Link>
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <Link href="/cart" className="inline-flex items-center text-sm text-muted-foreground hover:text-primary mb-6">
        <ArrowLeft className="h-4 w-4 mr-2" /> Back to Cart
      </Link>

      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">Checkout</h1>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <div className="grid lg:grid-cols-3 gap-8">
              {/* Left Column: Forms */}
              <div className="lg:col-span-2 space-y-8">
                {/* Shipping Information */}
                <div className="bg-card border rounded-lg p-6">
                  <ShippingForm disabled={orderMutation.isPending} />
                </div>

                {/* Payment Method */}
                <div className="bg-card border rounded-lg p-6">
                  <PaymentMethodSelector disabled={orderMutation.isPending} />
                </div>
              </div>

              {/* Right Column: Order Summary */}
              <div className="lg:col-span-1">
                <div className="sticky top-24 space-y-4">
                  <OrderSummary 
                    cart={cart}
                    isLoading={isCartLoading}
                    shippingCost={0}
                  />

                  <Separator />

                  {/* Submit Button */}
                  <Button 
                    type="submit" 
                    size="lg" 
                    className="w-full text-lg h-14" 
                    disabled={orderMutation.isPending || isCartLoading}
                  >
                    {orderMutation.isPending ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Processing...
                      </>
                    ) : (
                      `Place Order ${cart ? `($${cart.totalAmount.toFixed(2)})` : ""}`
                    )}
                  </Button>

                  <p className="text-xs text-center text-muted-foreground">
                    By placing this order, you agree to our Terms of Service and Privacy Policy
                  </p>
                </div>
              </div>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
}
