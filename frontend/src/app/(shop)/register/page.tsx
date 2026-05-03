"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import Cookies from "js-cookie";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Loader2, Eye, EyeOff, Sparkles } from "lucide-react";
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
import api from "@/lib/axios";

// Validation Schema
const formSchema = z.object({
  firstname: z.string().min(2, {
    message: "First name must be at least 2 characters.",
  }),
  lastname: z.string().min(2, {
    message: "Last name must be at least 2 characters.",
  }),
  email: z.string().email({
    message: "Please enter a valid email address.",
  }),
  password: z.string().min(6, {
    message: "Password must be at least 6 characters.",
  }),
});

const benefits = [
  { icon: "✦", title: "AI Style Profiling", desc: "Get personalized recommendations powered by AI" },
  { icon: "◈", title: "Virtual Try-On", desc: "See how clothes look on you before buying" },
  { icon: "◎", title: "Early Access", desc: "Be the first to shop new drops and exclusive items" },
];

export default function RegisterPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [benefitIndex, setBenefitIndex] = useState(0);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      firstname: "",
      lastname: "",
      email: "",
      password: "",
    },
  });

  async function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true);
    try {
      const response = await api.post("/auth/register", values);

      const access_token = response.data?.access_token || response.data;

      // Store token and login immediately
      if (typeof access_token === "string") {
        Cookies.set("token", access_token, { expires: 1 });
      }

      toast.success("Account created successfully!", {
        duration: 2000,
      });

      setTimeout(() => {
        router.push("/");
        router.refresh();
      }, 1000);

    } catch (error: unknown) {
      console.error("Register error:", error);
      let message = "Registration failed. Please try again.";
      if (typeof error === 'object' && error !== null && 'response' in error) {
         const resp = (error as { response?: { data?: { message?: string } } }).response;
         message = resp?.data?.message || message;
      }
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="w-full lg:grid lg:grid-cols-2 min-h-[calc(100dvh-4rem)]">
      {/* Visual Side */}
      <div className="hidden lg:flex flex-col items-center justify-center bg-zinc-950 text-white p-10 relative overflow-hidden">
        {/* Background pattern */}
        <div className="absolute inset-0 opacity-10"
          style={{
            backgroundImage: `radial-gradient(circle at 1px 1px, white 1px, transparent 0)`,
            backgroundSize: '40px 40px',
          }}
        />
        <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-br from-zinc-900 via-zinc-950 to-black" />

        {/* Floating decorative elements */}
        <div className="absolute top-1/4 right-1/4 w-64 h-64 bg-white/5 rounded-full blur-[80px]" />
        <div className="absolute bottom-1/3 left-1/3 w-96 h-96 bg-white/3 rounded-full blur-[100px]" />

        <div className="relative z-20 flex flex-col items-center text-center space-y-8 max-w-md">
          <div className="w-16 h-16 rounded-2xl bg-white/10 backdrop-blur-md border border-white/10 flex items-center justify-center mb-4">
            <Sparkles className="h-8 w-8 text-white/60" />
          </div>

          <AnimatePresence mode="wait">
            <motion.div
              key={benefitIndex}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.5 }}
              className="space-y-4"
            >
              <span className="text-5xl">{benefits[benefitIndex].icon}</span>
              <h2 className="text-2xl font-bold tracking-tight">{benefits[benefitIndex].title}</h2>
              <p className="text-base text-white/50 font-light">
                {benefits[benefitIndex].desc}
              </p>
            </motion.div>
          </AnimatePresence>

          <div className="flex gap-2 pt-4">
            {benefits.map((_, i) => (
              <button
                key={i}
                onClick={() => setBenefitIndex(i)}
                className={`h-1.5 rounded-full transition-all duration-300 ${
                  i === benefitIndex ? "w-8 bg-white" : "w-1.5 bg-white/30 hover:bg-white/50"
                }`}
                aria-label={`Benefit ${i + 1}`}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Form Side */}
      <div className="flex items-center justify-center py-12 px-4 relative">
        <div className="mx-auto grid w-full max-w-[400px] gap-6">
          <div className="grid gap-2 text-center">
            <h1 className="text-3xl font-bold tracking-tight">Create Account</h1>
            <p className="text-muted-foreground">
              Enter your information below to create your account
            </p>
          </div>

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="firstname"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>First Name</FormLabel>
                        <FormControl>
                          <Input placeholder="John" autoComplete="given-name" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="lastname"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Last Name</FormLabel>
                        <FormControl>
                          <Input placeholder="Doe" autoComplete="family-name" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
              </div>

              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input placeholder="name@example.com" type="email" autoComplete="email" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Input
                          type={showPassword ? "text" : "password"}
                          placeholder="Create a strong password"
                          autoComplete="new-password"
                          {...field}
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword(!showPassword)}
                          className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                        >
                          {showPassword ? (
                            <EyeOff className="h-4 w-4" />
                          ) : (
                            <Eye className="h-4 w-4" />
                          )}
                        </button>
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="w-full h-11" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Creating account...
                  </>
                ) : (
                  "Create Account"
                )}
              </Button>
            </form>
          </Form>

          <div className="text-center text-sm">
            Already have an account?{" "}
            <Link href="/login" className="underline font-medium hover:text-primary">
              Log in
            </Link>
          </div>

          <div className="relative my-2">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t border-border" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-background px-2 text-muted-foreground tracking-wider">
                Or continue with
              </span>
            </div>
          </div>

          <Button
            variant="outline"
            type="button"
            className="w-full h-11 border-border hover:bg-secondary transition-colors"
            onClick={() => {
              window.location.href = 'http://localhost:8080/oauth2/authorization/google';
            }}
          >
            <svg className="h-5 w-5 mr-2" viewBox="0 0 24 24">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
            </svg>
            Join with Google
          </Button>
        </div>
      </div>
    </div>
  );
}
