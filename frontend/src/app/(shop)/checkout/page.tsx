"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useRouter } from "next/navigation";
import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { Loader2, AlertCircle } from "lucide-react";
import Link from "next/link";
import { toast } from "sonner";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import { Form } from "@/components/ui/form";
import { Alert, AlertDescription } from "@/components/ui/alert";

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
  fullName: z.string().min(2, "Full Name must be at least 2 characters").max(100, "Full Name too long"),
  phone: z.string().regex(/^[0-9+\-\s()]{10,15}$/, "Invalid phone number format").min(10, "Phone number must be at least 10 digits"),
  address: z.string().min(10, "Address must be at least 10 characters").max(200, "Address too long"),
  note: z.string().max(500, "Note too long").optional(),
  paymentMethod: z.nativeEnum(PaymentMethod),
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
    onError: (error: unknown) => {
      console.error("Order failed:", error);
      const axiosError = error as { response?: { data?: { message?: string } } };
      toast.error(axiosError.response?.data?.message || "Failed to place order. Please try again.");
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
    <div className="container mx-auto px-4 py-12 md:py-24">
      <div className="max-w-5xl mx-auto">
        <div className="mb-12 flex items-center justify-between">
            <h1 className="text-3xl md:text-5xl font-black uppercase tracking-widest">Checkout</h1>
            <Button variant="ghost" asChild className="tracking-widest uppercase text-xs font-bold text-muted-foreground hover:text-foreground">
                <Link href="/shop">Continue Shopping</Link>
            </Button>
        </div>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <div className="flex flex-col lg:flex-row gap-16">
              {/* Left Column: Forms */}
              <div className="w-full lg:w-3/5 space-y-12">
                {/* Contact & Shipping */}
                <div className="space-y-6">
                    <div className="flex items-center justify-between border-b border-border pb-4">
                        <h2 className="text-lg font-semibold uppercase tracking-widest">1. Shipping & Contact</h2>
                    </div>
                    <div className="pt-4">
                        <ShippingForm disabled={orderMutation.isPending} />
                    </div>
                </div>

                {/* Payment Method */}
                <div className="space-y-6">
                    <div className="flex items-center justify-between border-b border-border pb-4">
                        <h2 className="text-lg font-semibold uppercase tracking-widest">2. Payment</h2>
                    </div>
                    <div className="pt-4">
                        <PaymentMethodSelector disabled={orderMutation.isPending} />
                    </div>
                </div>
              </div>

              {/* Right Column: Order Summary */}
              <div className="w-full lg:w-2/5">
                <div className="sticky top-28 bg-secondary/20 p-8">
                  <h2 className="text-lg font-semibold uppercase tracking-widest mb-6">Order Summary</h2>
                  
                  <OrderSummary 
                    cart={cart}
                    isLoading={isCartLoading}
                    shippingCost={0}
                  />

                  <div className="mt-8 pt-8 border-t border-border">
                    <div className="flex justify-between items-end mb-6">
                        <span className="text-sm font-semibold uppercase tracking-widest text-muted-foreground">Total</span>
                        <span className="text-3xl font-black">${cart?.totalAmount?.toFixed(2) || "0.00"}</span>
                    </div>

                    <Button 
                        type="submit" 
                        size="lg" 
                        className="w-full text-sm font-bold tracking-widest uppercase h-14 rounded-none bg-foreground text-background hover:bg-foreground/90 transition-all" 
                        disabled={orderMutation.isPending || isCartLoading}
                    >
                        {orderMutation.isPending ? (
                        <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Processing...
                        </>
                        ) : (
                        "Place Order"
                        )}
                    </Button>

                    <p className="text-xs text-center text-muted-foreground mt-4 font-light">
                        By placing this order, you agree to our <Link href="/terms" className="underline underline-offset-2 hover:text-foreground">Terms</Link> & <Link href="/privacy" className="underline underline-offset-2 hover:text-foreground">Privacy Policy</Link>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
}
