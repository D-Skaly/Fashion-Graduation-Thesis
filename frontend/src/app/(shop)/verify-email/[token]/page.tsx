"use client";

import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { Loader2, Mail, CheckCircle2, XCircle, RefreshCw, ArrowRight } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import { apiService } from "@/lib/apiService";

const resendSchema = z.object({
  email: z.string().email({
    message: "Please enter a valid email address.",
  }),
});

type VerificationStatus = "loading" | "success" | "error" | "idle";

export default function VerifyEmailPage() {
  const router = useRouter();
  const params = useParams<{ token: string }>();
  const [status, setStatus] = useState<VerificationStatus>("loading");
  const [showResendForm, setShowResendForm] = useState(false);

  const resendForm = useForm<z.infer<typeof resendSchema>>({
    resolver: zodResolver(resendSchema),
    defaultValues: {
      email: "",
    },
  });

  useEffect(() => {
    if (params.token) {
      verifyEmail(params.token);
    } else {
      setStatus("idle");
    }
  }, [params.token]);

  async function verifyEmail(token: string) {
    setStatus("loading");
    try {
      await apiService.auth.verifyEmail(token);
      setStatus("success");
      toast.success("Email verified successfully!");

      // Auto-redirect after 3 seconds
      setTimeout(() => {
        router.push("/login?verified=true");
      }, 3000);
    } catch (error) {
      setStatus("error");
      toast.error("Verification failed. The link may have expired.");
    }
  }

  async function onResend(values: z.infer<typeof resendSchema>) {
    try {
      await apiService.auth.resendVerification(values.email);
      toast.success("Verification email sent! Please check your inbox.");
      setShowResendForm(false);
    } catch (error) {
      toast.error("Failed to resend verification email. Please try again.");
    }
  }

  return (
    <div className="w-full lg:grid lg:grid-cols-2 min-h-[calc(100dvh-4rem)]">
      {/* Visual Side */}
      <div className="hidden lg:flex flex-col items-center justify-center bg-zinc-950 text-white p-10 relative overflow-hidden">
        <div className="absolute inset-0 opacity-10"
          style={{
            backgroundImage: `radial-gradient(circle at 1px 1px, white 1px, transparent 0)`,
            backgroundSize: '40px 40px',
          }}
        />
        <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-br from-zinc-900 via-zinc-950 to-black" />

        <div className="relative z-20 flex flex-col items-center text-center space-y-8 max-w-md">
          <div className="w-16 h-16 rounded-2xl bg-white/10 backdrop-blur-md border border-white/10 flex items-center justify-center mb-4">
            <Mail className="h-8 w-8 text-white/60" />
          </div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="space-y-4"
          >
            <h2 className="text-3xl font-light">Verify Your Email</h2>
            <p className="text-white/60 leading-relaxed">
              We've sent you a verification email. Please check your inbox and click the link to verify your account.
            </p>
          </motion.div>
        </div>
      </div>

      {/* Content Side */}
      <div className="flex items-center justify-center py-12 px-4">
        <div className="mx-auto grid w-full max-w-[400px] gap-6">
          <AnimatePresence mode="wait">
            {status === "loading" && (
              <motion.div
                key="loading"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="text-center space-y-4"
              >
                <div className="w-16 h-16 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center mx-auto">
                  <Loader2 className="h-8 w-8 text-blue-600 dark:text-blue-400 animate-spin" />
                </div>
                <h1 className="text-2xl font-bold">Verifying...</h1>
                <p className="text-muted-foreground">
                  Please wait while we verify your email address.
                </p>
              </motion.div>
            )}

            {status === "success" && (
              <motion.div
                key="success"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="text-center space-y-4"
              >
                <div className="w-16 h-16 rounded-full bg-green-100 dark:bg-green-900/30 flex items-center justify-center mx-auto">
                  <CheckCircle2 className="h-8 w-8 text-green-600 dark:text-green-400" />
                </div>
                <div className="space-y-2">
                  <h1 className="text-2xl font-bold">Email Verified!</h1>
                  <p className="text-muted-foreground">
                    Your email has been successfully verified. Redirecting you to login...
                  </p>
                </div>
                <Link href="/login?verified=true">
                  <Button className="mt-4">
                    Continue to Login
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                </Link>
              </motion.div>
            )}

            {(status === "error" || status === "idle") && (
              <motion.div
                key="error-or-idle"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="space-y-6"
              >
                {status === "error" && (
                  <div className="text-center space-y-4">
                    <div className="w-16 h-16 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center mx-auto">
                      <XCircle className="h-8 w-8 text-red-600 dark:text-red-400" />
                    </div>
                    <div className="space-y-2">
                      <h1 className="text-2xl font-bold">Verification Failed</h1>
                      <p className="text-muted-foreground">
                        The verification link is invalid or has expired.
                      </p>
                    </div>
                  </div>
                )}

                {status === "idle" && (
                  <div className="text-center space-y-2">
                    <h1 className="text-2xl font-bold">Email Verification</h1>
                    <p className="text-muted-foreground">
                      Enter your email to resend the verification link
                    </p>
                  </div>
                )}

                {!showResendForm ? (
                  <Button
                    variant="outline"
                    className="w-full h-11"
                    onClick={() => setShowResendForm(true)}
                  >
                    <RefreshCw className="mr-2 h-4 w-4" />
                    Resend Verification Email
                  </Button>
                ) : (
                  <Form {...resendForm}>
                    <form onSubmit={resendForm.handleSubmit(onResend)} className="space-y-4">
                      <FormField
                        control={resendForm.control}
                        name="email"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Email</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                <Input
                                  placeholder="name@example.com"
                                  type="email"
                                  className="pl-10"
                                  {...field}
                                />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <Button type="submit" className="w-full h-11">
                        Send Verification Email
                      </Button>
                    </form>
                  </Form>
                )}

                <div className="text-center">
                  <Link
                    href="/login"
                    className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground transition-colors"
                  >
                    <ArrowRight className="mr-2 h-4 w-4 rotate-180" />
                    Back to Login
                  </Link>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}
