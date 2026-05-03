"use client";

import { RotateCcw, Download, Truck, CheckCircle2, Package, Mail } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import Link from "next/link";

export default function ReturnsPage() {
  return (
    <div className="container mx-auto px-4 py-12 max-w-4xl">
      {/* Header */}
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-5xl font-bold mb-4">Returns & Exchanges</h1>
        <p className="text-muted-foreground max-w-2xl mx-auto">
          We want you to love your purchase. If something isn't right, we're here to help.
        </p>
      </div>

      {/* Return Policy */}
      <Card className="mb-8">
        <CardHeader>
          <CardTitle className="text-2xl flex items-center gap-2">
            <CheckCircle2 className="h-6 w-6 text-primary" />
            Return Policy
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4 text-muted-foreground">
            <p>
              We offer a <strong className="text-foreground">30-day return policy</strong> from the date of delivery. 
              Items must be unworn, unwashed, and in their original packaging with all tags attached.
            </p>
            <p>
              Return shipping is <strong className="text-foreground">FREE</strong> for defective or incorrect items. 
              For other returns, a flat rate of $9.99 will be deducted from your refund.
            </p>
          </div>
        </CardContent>
      </Card>

      {/* Step-by-Step Process */}
      <div className="mb-12">
        <h2 className="text-2xl font-bold mb-8 text-center">How to Return</h2>
        <div className="grid md:grid-cols-4 gap-6">
          {[
            {
              step: "1",
              icon: Package,
              title: "Initiate Return",
              description: "Visit our Track Order page or contact support to start your return.",
            },
            {
              step: "2",
              icon: Truck,
              title: "Pack & Ship",
              description: "Pack items securely in original packaging and ship to our returns center.",
            },
            {
              step: "3",
              icon: CheckCircle2,
              title: "We Inspect",
              description: "Once received, we inspect items within 2 business days.",
            },
            {
              step: "4",
              icon: RotateCcw,
              title: "Refund Issued",
              description: "Refunds are processed within 5-7 business days after inspection.",
            },
          ].map((step) => {
            const Icon = step.icon;
            return (
              <Card key={step.step}>
                <CardContent className="pt-6 text-center">
                  <div className="w-12 h-12 mx-auto bg-primary/10 rounded-full flex items-center justify-center mb-4">
                    <Icon className="h-6 w-6 text-primary" />
                  </div>
                  <div className="w-8 h-8 mx-auto bg-primary text-primary-foreground rounded-full flex items-center justify-center text-sm font-bold mb-3">
                    {step.step}
                  </div>
                  <h3 className="font-bold mb-2">{step.title}</h3>
                  <p className="text-sm text-muted-foreground">{step.description}</p>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Return Form Download */}
      <Card className="mb-8">
        <CardContent className="pt-6">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
                <Download className="h-6 w-6 text-primary" />
              </div>
              <div>
                <h3 className="font-bold">Return Form</h3>
                <p className="text-sm text-muted-foreground">
                  Download and complete our return form to include with your shipment.
                </p>
              </div>
            </div>
            <Button className="shrink-0">
              <Download className="mr-2 h-4 w-4" />
              Download Form
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Non-Returnable Items */}
      <Card className="mb-8">
        <CardHeader>
          <CardTitle>Non-Returnable Items</CardTitle>
        </CardHeader>
        <CardContent>
          <ul className="space-y-2 text-muted-foreground">
            {[
              "Intimate apparel (underwear, swimwear) for hygiene reasons",
              "Final sale items marked as non-returnable",
              "Items damaged due to customer misuse",
              "Gift cards",
            ].map((item, i) => (
              <li key={i} className="flex items-start gap-2">
                <span className="text-primary mt-1">•</span>
                {item}
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>

      {/* CTA */}
      <div className="text-center bg-secondary/20 rounded-3xl p-12">
        <h2 className="text-2xl font-bold mb-4">Need Help?</h2>
        <p className="text-muted-foreground mb-6 max-w-md mx-auto">
          Our support team is ready to assist you with any return or exchange questions.
        </p>
        <div className="flex gap-4 justify-center">
          <Button asChild>
            <Link href="/contact">
              <Mail className="mr-2 h-4 w-4" />
              Contact Support
            </Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/faq">View FAQ</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
