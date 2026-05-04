"use client";

import { Quote, Sparkles } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";
import Link from "next/link";

export default function AboutUsPage() {
  return (
    <div className="container mx-auto px-4 py-12 max-w-5xl">
      {/* Hero Section */}
      <div className="text-center mb-16">
        <h1 className="text-4xl md:text-5xl font-bold mb-4">About Us</h1>
        <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
          Redefining fashion through innovation, quality, and sustainable practices.
        </p>
      </div>

      {/* Mission/Vision */}
      <div className="grid md:grid-cols-2 gap-8 mb-16">
        <Card>
          <CardContent className="pt-6">
            <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center mb-4">
              <Sparkles className="h-6 w-6 text-primary" />
            </div>
            <h2 className="text-2xl font-bold mb-3">Our Mission</h2>
            <p className="text-muted-foreground leading-relaxed">
              To empower individuals through fashion that combines style, comfort, and sustainability. 
              We believe that what you wear should reflect who you are while respecting the world we live in.
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center mb-4">
              <Quote className="h-6 w-6 text-primary" />
            </div>
            <h2 className="text-2xl font-bold mb-3">Our Vision</h2>
            <p className="text-muted-foreground leading-relaxed">
              To become the leading fashion destination that pioneers the integration of technology 
              and style, making personalized fashion accessible to everyone through innovative solutions.
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Story Section */}
      <div className="mb-16">
        <h2 className="text-3xl font-bold mb-6 text-center">Our Story</h2>
        <div className="grid md:grid-cols-2 gap-8 items-center">
          <div className="relative aspect-square rounded-2xl overflow-hidden bg-muted">
            <div className="absolute inset-0 flex items-center justify-center">
              <Sparkles className="h-16 w-16 text-muted-foreground" />
            </div>
          </div>
          <div className="space-y-4">
            <p className="text-muted-foreground leading-relaxed">
              Founded with a vision to revolutionize the fashion industry, we started as a small 
              boutique with big dreams. Today, we combine cutting-edge technology with timeless 
              style to bring you a unique shopping experience.
            </p>
            <p className="text-muted-foreground leading-relaxed">
              Our journey began when we realized that traditional shopping couldn’t keep up with 
              modern needs. We embraced AI, virtual try-ons, and body measurement technology 
              to bridge the gap between online shopping and perfect fit.
            </p>
          </div>
        </div>
      </div>

      {/* Values */}
      <div className="mb-16">
        <h2 className="text-3xl font-bold mb-8 text-center">Our Values</h2>
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {[
            {
              title: "Quality First",
              description: "Every piece is carefully curated and quality-checked before reaching you.",
            },
            {
              title: "Sustainable Fashion",
              description: "We prioritize eco-friendly materials and ethical manufacturing processes.",
            },
            {
              title: "Innovation",
              description: "Leveraging AI and AR to enhance your shopping experience.",
            },
            {
              title: "Customer Centric",
              description: "Your satisfaction drives everything we do, from design to delivery.",
            },
            {
              title: "Transparency",
              description: "Open about our processes, pricing, and business practices.",
            },
            {
              title: "Community",
              description: "Building a fashion community that celebrates individual expression.",
            },
          ].map((value, index) => (
            <Card key={index}>
              <CardContent className="pt-6">
                <h3 className="font-bold mb-2">{value.title}</h3>
                <p className="text-sm text-muted-foreground">{value.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* CTA */}
      <div className="text-center bg-secondary/20 rounded-3xl p-12">
        <h2 className="text-2xl font-bold mb-4">Join Our Journey</h2>
        <p className="text-muted-foreground mb-6 max-w-md mx-auto">
          Experience the future of fashion today. Shop our latest collections or join our community.
        </p>
        <div className="flex gap-4 justify-center">
          <Button asChild size="lg">
            <Link href="/shop">Shop Now</Link>
          </Button>
          <Button variant="outline" size="lg" asChild>
            <Link href="/contact">Contact Us</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
