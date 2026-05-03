"use client";

import { FileText, Scale, AlertTriangle, Ban, Gavel } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";

export default function TermsOfServicePage() {
  return (
    <div className="container mx-auto px-4 py-12 max-w-4xl">
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-5xl font-bold mb-4">Terms of Service</h1>
        <p className="text-muted-foreground">
          Last updated: {new Date().toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" })}
        </p>
      </div>

      <div className="space-y-8">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <FileText className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">1. Acceptance of Terms</h2>
            </div>
            <p className="text-muted-foreground leading-relaxed">
              By accessing or using Fashion Store's website and services, you agree to be bound by these Terms of Service. 
              If you do not agree to these terms, please do not use our services. We reserve the right to update 
              these terms at any time without prior notice.
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <Scale className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">2. User Accounts</h2>
            </div>
            <div className="space-y-4 text-muted-foreground">
              <p>
                To access certain features, you must register for an account. You agree to:
              </p>
              <ul className="space-y-2 list-inside list-disc">
                <li>Provide accurate and complete registration information</li>
                <li>Maintain the security of your account credentials</li>
                <li>Promptly update any changes to your information</li>
                <li>Accept responsibility for all activities under your account</li>
              </ul>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <AlertTriangle className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">3. Product Information</h2>
            </div>
            <p className="text-muted-foreground leading-relaxed">
              We strive to display accurate product information, including colors and images. However, we do not guarantee 
              that all product descriptions or images are accurate, complete, or error-free. Colors may vary due to monitor settings. 
              All prices are subject to change without notice.
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <Ban className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">4. Prohibited Uses</h2>
            </div>
            <p className="text-muted-foreground mb-4">You agree not to:</p>
            <ul className="space-y-2 list-inside list-disc text-muted-foreground">
              <li>Use our site for any unlawful purpose</li>
              <li>Attempt to gain unauthorized access to our systems</li>
              <li>Interfere with the proper working of the site</li>
              <li>Use our trademarks or content without permission</li>
              <li>Collect user information without consent</li>
            </ul>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <Gavel className="h-5 w-5 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">5. Limitation of Liability</h2>
            </div>
            <p className="text-muted-foreground leading-relaxed">
              Fashion Store shall not be liable for any indirect, incidental, special, consequential, or punitive damages, 
              or any loss of profits or revenues, whether incurred directly or indirectly, or any loss of data, use, 
              goodwill, or other intangible losses resulting from your use of our services.
            </p>
          </CardContent>
        </Card>

        <Separator />

        <Card className="bg-secondary/20">
          <CardContent className="pt-6">
            <h3 className="font-bold mb-2">Contact Information</h3>
            <p className="text-muted-foreground">
              If you have questions about these Terms of Service, please contact us at:
              <br />
              Email: legal@fashionstore.com
              <br />
              Mail: 123 Fashion Street, New York, NY 10001
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
