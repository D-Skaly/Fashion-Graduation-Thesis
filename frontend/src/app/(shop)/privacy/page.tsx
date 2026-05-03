"use client";

import { Shield, Lock, Eye, Share2 } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";

export default function PrivacyPolicyPage() {
  return (
    <div className="container mx-auto px-4 py-12 max-w-4xl">
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-5xl font-bold mb-4">Privacy Policy</h1>
        <p className="text-muted-foreground">
          Last updated: {new Date().toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" })}
        </p>
      </div>

      <div className="space-y-8">
        <Card>
          <CardContent className="pt-6">
            <p className="text-muted-foreground leading-relaxed">
              At Fashion Store, we are committed to protecting your privacy. This Privacy Policy explains 
              how we collect, use, disclose, and safeguard your information when you visit our website.
            </p>
          </CardContent>
        </Card>

        <div>
          <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
            <Shield className="h-6 w-6 text-primary" />
            Information We Collect
          </h2>
          <div className="space-y-4">
            {[
              {
                title: "Personal Information",
                items: [
                  "Name and email address when you create an account",
                  "Shipping and billing addresses for orders",
                  "Payment information (processed securely through third-party providers)",
                  "Phone number for order updates",
                ],
              },
              {
                title: "Automatically Collected Information",
                items: [
                  "IP address and browser type",
                  "Pages visited and time spent on site",
                  "Referring website information",
                  "Device information and cookies",
                ],
              },
            ].map((section, index) => (
              <Card key={index}>
                <CardContent className="pt-6">
                  <h3 className="font-bold mb-3">{section.title}</h3>
                  <ul className="space-y-2 text-muted-foreground">
                    {section.items.map((item, i) => (
                      <li key={i} className="flex items-start gap-2">
                        <span className="text-primary mt-1.5">•</span>
                        {item}
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <Separator />

        <div>
          <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
            <Eye className="h-6 w-6 text-primary" />
            How We Use Your Information
          </h2>
          <Card>
            <CardContent className="pt-6">
              <ul className="space-y-2 text-muted-foreground">
                {[
                  "Process and fulfill your orders",
                  "Communicate order updates and support",
                  "Send promotional emails (you can opt-out anytime)",
                  "Improve our website and personalize your experience",
                  "Prevent fraud and enhance security",
                ].map((item, i) => (
                  <li key={i} className="flex items-start gap-2">
                    <span className="text-primary mt-1.5">•</span>
                    {item}
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        </div>

        <Separator />

        <div>
          <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
            <Share2 className="h-6 w-6 text-primary" />
            Information Sharing
          </h2>
          <Card>
            <CardContent className="pt-6">
              <p className="text-muted-foreground mb-4">
                We do not sell your personal information. We may share information with:
              </p>
              <ul className="space-y-2 text-muted-foreground">
                {[
                  "Service providers (shipping, payment processing)",
                  "Legal authorities when required by law",
                  "Business transfers (merger, acquisition)",
                ].map((item, i) => (
                  <li key={i} className="flex items-start gap-2">
                    <span className="text-primary mt-1.5">•</span>
                    {item}
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        </div>

        <Separator />

        <div>
          <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
            <Lock className="h-6 w-6 text-primary" />
            Your Rights
          </h2>
          <Card>
            <CardContent className="pt-6">
              <p className="text-muted-foreground mb-4">
                You have the right to:
              </p>
              <ul className="space-y-2 text-muted-foreground">
                {[
                  "Access and update your personal information",
                  "Request deletion of your account and data",
                  "Opt-out of marketing communications",
                  "Request a copy of your data",
                ].map((item, i) => (
                  <li key={i} className="flex items-start gap-2">
                    <span className="text-primary mt-1.5">•</span>
                    {item}
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        </div>

        <Card className="bg-secondary/20">
          <CardContent className="pt-6">
            <h3 className="font-bold mb-2">Contact Us</h3>
            <p className="text-muted-foreground">
              If you have questions about this Privacy Policy, please contact us at:
              <br />
              Email: privacy@fashionstore.com
              <br />
              Mail: 123 Fashion Street, New York, NY 10001
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
