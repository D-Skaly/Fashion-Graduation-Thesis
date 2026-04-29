"use client";

import { useState } from "react";
import { User, Package, Heart, Sparkles, Settings, CreditCard, LogOut } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

export default function AccountPage() {
  const [activeTab, setActiveTab] = useState("profile");

  return (
    <div className="container mx-auto px-4 py-12 md:py-24">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl md:text-5xl font-black uppercase tracking-widest mb-12">My Account</h1>

        <Tabs defaultValue="profile" className="flex flex-col md:flex-row gap-12" onValueChange={setActiveTab}>
          {/* Sidebar Navigation */}
          <TabsList className="flex md:flex-col justify-start h-auto bg-transparent space-y-0 space-x-2 md:space-x-0 md:space-y-2 w-full md:w-64 flex-wrap md:flex-nowrap border-b md:border-b-0 md:border-r border-border pb-4 md:pb-0 md:pr-8">
            <TabsTrigger 
                value="profile" 
                className="justify-start px-4 py-3 rounded-none data-[state=active]:bg-secondary/30 data-[state=active]:border-l-2 data-[state=active]:border-foreground data-[state=active]:shadow-none transition-all uppercase tracking-widest text-xs font-bold"
            >
              <User className="mr-3 h-4 w-4" /> Profile Info
            </TabsTrigger>
            <TabsTrigger 
                value="ai-style" 
                className="justify-start px-4 py-3 rounded-none data-[state=active]:bg-primary/5 data-[state=active]:border-l-2 data-[state=active]:border-primary data-[state=active]:shadow-none transition-all uppercase tracking-widest text-xs font-bold text-primary"
            >
              <Sparkles className="mr-3 h-4 w-4" /> AI Style Profile
            </TabsTrigger>
            <TabsTrigger 
                value="orders" 
                className="justify-start px-4 py-3 rounded-none data-[state=active]:bg-secondary/30 data-[state=active]:border-l-2 data-[state=active]:border-foreground data-[state=active]:shadow-none transition-all uppercase tracking-widest text-xs font-bold"
            >
              <Package className="mr-3 h-4 w-4" /> Order History
            </TabsTrigger>
            <TabsTrigger 
                value="wishlist" 
                className="justify-start px-4 py-3 rounded-none data-[state=active]:bg-secondary/30 data-[state=active]:border-l-2 data-[state=active]:border-foreground data-[state=active]:shadow-none transition-all uppercase tracking-widest text-xs font-bold"
            >
              <Heart className="mr-3 h-4 w-4" /> Wishlist
            </TabsTrigger>

            <Separator className="my-4 hidden md:block" />
            
            <Button variant="ghost" className="justify-start px-4 py-3 rounded-none hover:bg-destructive/10 hover:text-destructive transition-all uppercase tracking-widest text-xs font-bold text-muted-foreground w-full">
                <LogOut className="mr-3 h-4 w-4" /> Sign Out
            </Button>
          </TabsList>

          {/* Content Area */}
          <div className="flex-1">
            <TabsContent value="profile" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
              <div className="space-y-4">
                <h2 className="text-2xl font-bold uppercase tracking-wider">Profile Information</h2>
                <p className="text-muted-foreground font-light">Manage your personal details and account settings.</p>
              </div>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="space-y-2">
                    <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Full Name</label>
                    <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">John Doe</div>
                </div>
                <div className="space-y-2">
                    <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Email</label>
                    <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">john.doe@example.com</div>
                </div>
              </div>
              <Button className="rounded-none tracking-widest uppercase font-bold px-8 h-12 bg-foreground text-background">Edit Profile</Button>
            </TabsContent>

            <TabsContent value="ai-style" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
              <div className="flex items-center gap-3 mb-2">
                <div className="h-10 w-10 bg-primary/10 rounded-full flex items-center justify-center text-primary">
                    <Sparkles className="h-5 w-5" />
                </div>
                <div>
                    <h2 className="text-2xl font-bold uppercase tracking-wider">AI Style Profile</h2>
                    <p className="text-muted-foreground font-light text-sm">Your personalized fashion genome curated by AI.</p>
                </div>
              </div>
              
              <div className="grid md:grid-cols-2 gap-6">
                <div className="border border-border p-6 bg-secondary/5 relative overflow-hidden group">
                    <div className="absolute -right-4 -top-4 w-24 h-24 bg-primary/5 rounded-full blur-xl group-hover:bg-primary/10 transition-colors" />
                    <h3 className="font-bold uppercase tracking-widest text-sm mb-4">Preferred Aesthetics</h3>
                    <div className="flex flex-wrap gap-2">
                        <span className="px-3 py-1 border border-border text-xs uppercase tracking-wider">Urban Minimalist</span>
                        <span className="px-3 py-1 border border-border text-xs uppercase tracking-wider">Monochrome</span>
                        <span className="px-3 py-1 border border-primary/20 bg-primary/5 text-primary text-xs uppercase tracking-wider">Techwear</span>
                    </div>
                </div>
                
                <div className="border border-border p-6 bg-secondary/5 relative overflow-hidden group">
                    <div className="absolute -right-4 -top-4 w-24 h-24 bg-primary/5 rounded-full blur-xl group-hover:bg-primary/10 transition-colors" />
                    <h3 className="font-bold uppercase tracking-widest text-sm mb-4">Size Predictions</h3>
                    <div className="space-y-3">
                        <div className="flex justify-between items-center text-sm">
                            <span className="text-muted-foreground">Tops</span>
                            <span className="font-bold">Medium (98% match)</span>
                        </div>
                        <div className="flex justify-between items-center text-sm">
                            <span className="text-muted-foreground">Bottoms</span>
                            <span className="font-bold">32W / 32L</span>
                        </div>
                    </div>
                </div>
              </div>

              <div className="border border-primary/20 bg-primary/5 p-8 text-center space-y-4">
                 <h3 className="text-lg font-bold tracking-widest uppercase">Refine Your Style</h3>
                 <p className="text-sm font-light text-muted-foreground max-w-md mx-auto">Upload a recent full-body photo or link your Instagram to let our AI update your style profile for better recommendations.</p>
                 <Button variant="default" className="mt-4 rounded-none tracking-widest uppercase font-bold bg-primary text-primary-foreground hover:bg-primary/90">
                    Upload Photo
                 </Button>
              </div>
            </TabsContent>

            <TabsContent value="orders" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
              <div className="space-y-4">
                <h2 className="text-2xl font-bold uppercase tracking-wider">Order History</h2>
                <p className="text-muted-foreground font-light">View and track your recent purchases.</p>
              </div>
              <div className="border border-border flex items-center justify-center py-24 bg-secondary/5 text-muted-foreground">
                 No recent orders found.
              </div>
            </TabsContent>

            <TabsContent value="wishlist" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
              <div className="space-y-4">
                <h2 className="text-2xl font-bold uppercase tracking-wider">Your Wishlist</h2>
                <p className="text-muted-foreground font-light">Items you've saved for later.</p>
              </div>
              <div className="border border-border flex items-center justify-center py-24 bg-secondary/5 text-muted-foreground">
                 Your wishlist is currently empty.
              </div>
            </TabsContent>
          </div>
        </Tabs>
      </div>
    </div>
  );
}
