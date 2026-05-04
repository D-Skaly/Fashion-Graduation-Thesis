"use client";

import { useState } from "react";
import { Search, MessageCircle, Truck, CreditCard, RotateCcw } from "lucide-react";

import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface FAQItem {
  question: string;
  answer: string;
  category: "shipping" | "returns" | "payments" | "general";
}

const faqData: FAQItem[] = [
  // Shipping
  {
    question: "How long does shipping take?",
    answer: "Standard shipping takes 3-5 business days. Express shipping takes 1-2 business days. International orders may take 7-14 business days.",
    category: "shipping",
  },
  {
    question: "Do you offer free shipping?",
    answer: "Yes! We offer free standard shipping on all orders over $100. For orders under $100, standard shipping is $9.99.",
    category: "shipping",
  },
  {
    question: "Can I track my order?",
    answer: "Absolutely! Once your order ships, you'll receive a tracking number via email. You can also track your order through our Track Order page.",
    category: "shipping",
  },
  // Returns
  {
    question: "What is your return policy?",
    answer: "We offer a 30-day return policy. Items must be unworn, unwashed, and in original packaging with tags attached. Return shipping is free for defective items.",
    category: "returns",
  },
  {
    question: "How do I initiate a return?",
    answer: "Visit our Returns page and follow the step-by-step process. You'll need your order number and email address to start a return.",
    category: "returns",
  },
  {
    question: "How long do refunds take?",
    answer: "Once we receive your return, refunds are processed within 5-7 business days. The refund will appear in your original payment method within 3-5 business days after processing.",
    category: "returns",
  },
  // Payments
  {
    question: "What payment methods do you accept?",
    answer: "We accept Visa, MasterCard, American Express, PayPal, and Cash on Delivery (COD). We also support VNPay and MoMo for select regions.",
    category: "payments",
  },
  {
    question: "Is my payment information secure?",
    answer: "Yes, we use industry-standard SSL encryption to protect your payment information. We never store your full credit card details on our servers.",
    category: "payments",
  },
  // General
  {
    question: "How do I know what size to order?",
    answer: "Check our Size Guide page for detailed size charts and measurement instructions. You can also use our Virtual Try-On feature to see how items fit!",
    category: "general",
  },
  {
    question: "Can I modify or cancel my order?",
    answer: "You can modify or cancel your order within 1 hour of placing it. After that, the order enters processing and cannot be changed. Contact our support team immediately for assistance.",
    category: "general",
  },
];

const categories = [
  { id: "all", label: "All", icon: MessageCircle },
  { id: "shipping", label: "Shipping", icon: Truck },
  { id: "returns", label: "Returns", icon: RotateCcw },
  { id: "payments", label: "Payments", icon: CreditCard },
  { id: "general", label: "General", icon: MessageCircle },
];

export default function FAQPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState("all");

  const filteredFAQs = faqData.filter((faq) => {
    const matchesSearch =
      faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
      faq.answer.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = activeCategory === "all" || faq.category === activeCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="text-center mb-8">
        <h1 className="text-3xl md:text-4xl font-bold mb-4">Frequently Asked Questions</h1>
        <p className="text-muted-foreground">
          Find answers to common questions about our products and services
        </p>
      </div>

      {/* Search */}
      <div className="relative mb-6">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Search FAQs..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="pl-10"
        />
      </div>

      {/* Category Filter */}
      <div className="flex flex-wrap gap-2 mb-8">
        {categories.map((cat) => {
          const Icon = cat.icon;
          return (
            <Button
              key={cat.id}
              variant={activeCategory === cat.id ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveCategory(cat.id)}
            >
              <Icon className="mr-2 h-4 w-4" />
              {cat.label}
            </Button>
          );
        })}
      </div>

      {/* FAQ Items */}
      {filteredFAQs.length > 0 ? (
        <Accordion type="single" collapsible className="space-y-4">
          {filteredFAQs.map((faq, index) => (
            <Card key={index}>
              <AccordionItem value={`item-${index}`} className="border-none">
                <AccordionTrigger className="px-6 py-4 hover:no-underline">
                  <div className="flex items-center gap-3 text-left">
                    <Badge variant="secondary" className="capitalize">
                      {faq.category}
                    </Badge>
                    <span className="font-medium">{faq.question}</span>
                  </div>
                </AccordionTrigger>
                <AccordionContent className="px-6 pb-4">
                  <p className="text-muted-foreground leading-relaxed">{faq.answer}</p>
                </AccordionContent>
              </AccordionItem>
            </Card>
          ))}
        </Accordion>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <MessageCircle className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
            <p className="text-muted-foreground">No FAQs found matching your search.</p>
          </CardContent>
        </Card>
      )}

      {/* Contact CTA */}
      <Card className="mt-8 bg-secondary/20">
        <CardContent className="pt-6 text-center">
          <h3 className="font-bold mb-2">Still have questions?</h3>
          <p className="text-sm text-muted-foreground mb-4">
            Can’t find what you’re looking for? Our support team is here to help.
          </p>
          <Button asChild>
            <Link href="/contact">Contact Support</Link>
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
