"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { User, Package, Heart, Sparkles, LogOut, Loader2, Edit2, Save, X } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import Link from "next/link";
import Cookies from "js-cookie";
import api from "@/lib/axios";

// Types
interface UserProfile {
    id: string;
    firstname: string;
    lastname: string;
    email: string;
    role: string;
    createdAt?: string;
}

interface OrderSummary {
    id: string;
    totalAmount: number;
    status: string;
    createdAt: string;
    items: { productName: string; quantity: number }[];
}

interface WishlistItem {
    id: string;
    product: {
        id: string;
        name: string;
        basePrice: number;
        categoryName: string;
    };
    addedAt: string;
}

const fetchProfile = async (): Promise<UserProfile> => {
    const { data } = await api.get("/auth/me");
    return data;
};

const fetchMyOrders = async (): Promise<OrderSummary[]> => {
    const { data } = await api.get("/orders");
    return Array.isArray(data) ? data : data?.content || [];
};

const fetchWishlist = async (): Promise<WishlistItem[]> => {
    const { data } = await api.get("/wishlist");
    return Array.isArray(data) ? data : [];
};

const getStatusColor = (status: string) => {
    switch (status) {
        case "PENDING": return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400";
        case "CONFIRMED": return "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400";
        case "PROCESSING": return "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400";
        case "SHIPPED": return "bg-indigo-100 text-indigo-800 dark:bg-indigo-900/30 dark:text-indigo-400";
        case "DELIVERED": return "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400";
        case "CANCELLED": return "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400";
        default: return "bg-secondary text-muted-foreground";
    }
};

export default function AccountPage() {
    const router = useRouter();
    const queryClient = useQueryClient();
    const [isEditing, setIsEditing] = useState(false);
    const [editFirstname, setEditFirstname] = useState("");
    const [editLastname, setEditLastname] = useState("");

    // Fetch profile
    const { data: profile, isLoading: isProfileLoading } = useQuery({
        queryKey: ["profile"],
        queryFn: fetchProfile,
        retry: false,
    });

    // Fetch orders (last 5)
    const { data: orders, isLoading: isOrdersLoading } = useQuery({
        queryKey: ["my-orders-summary"],
        queryFn: fetchMyOrders,
        retry: false,
    });

    // Fetch wishlist
    const { data: wishlist, isLoading: isWishlistLoading } = useQuery({
        queryKey: ["wishlist"],
        queryFn: fetchWishlist,
        retry: false,
    });

    // Update profile mutation
    const updateProfileMutation = useMutation({
        mutationFn: async ({ firstname, lastname }: { firstname: string; lastname: string }) => {
            await api.put("/auth/profile", { firstname, lastname });
        },
        onSuccess: () => {
            toast.success("Profile updated successfully");
            queryClient.invalidateQueries({ queryKey: ["profile"] });
            setIsEditing(false);
        },
        onError: () => {
            toast.error("Failed to update profile");
        },
    });

    const handleLogout = () => {
        Cookies.remove("token");
        toast.success("Signed out successfully");
        router.push("/login");
        router.refresh();
    };

    const handleStartEdit = () => {
        if (profile) {
            setEditFirstname(profile.firstname || "");
            setEditLastname(profile.lastname || "");
        }
        setIsEditing(true);
    };

    const handleSaveEdit = () => {
        updateProfileMutation.mutate({ firstname: editFirstname, lastname: editLastname });
    };

    return (
        <div className="container mx-auto px-4 py-12 md:py-24">
            <div className="max-w-6xl mx-auto">
                <h1 className="text-3xl md:text-5xl font-black uppercase tracking-widest mb-12">My Account</h1>

                <Tabs defaultValue="profile" className="flex flex-col md:flex-row gap-12">
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
                            {orders && orders.length > 0 && (
                                <Badge variant="secondary" className="ml-auto text-[10px]">{orders.length}</Badge>
                            )}
                        </TabsTrigger>
                        <TabsTrigger
                            value="wishlist"
                            className="justify-start px-4 py-3 rounded-none data-[state=active]:bg-secondary/30 data-[state=active]:border-l-2 data-[state=active]:border-foreground data-[state=active]:shadow-none transition-all uppercase tracking-widest text-xs font-bold"
                        >
                            <Heart className="mr-3 h-4 w-4" /> Wishlist
                            {wishlist && wishlist.length > 0 && (
                                <Badge variant="secondary" className="ml-auto text-[10px]">{wishlist.length}</Badge>
                            )}
                        </TabsTrigger>

                        <Separator className="my-4 hidden md:block" />

                        <Button
                            variant="ghost"
                            className="justify-start px-4 py-3 rounded-none hover:bg-destructive/10 hover:text-destructive transition-all uppercase tracking-widest text-xs font-bold text-muted-foreground w-full"
                            onClick={handleLogout}
                        >
                            <LogOut className="mr-3 h-4 w-4" /> Sign Out
                        </Button>
                    </TabsList>

                    {/* Content Area */}
                    <div className="flex-1">
                        {/* Profile Tab */}
                        <TabsContent value="profile" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
                            <div className="space-y-4">
                                <h2 className="text-2xl font-bold uppercase tracking-wider">Profile Information</h2>
                                <p className="text-muted-foreground font-light">Manage your personal details and account settings.</p>
                            </div>

                            {isProfileLoading ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                    {[1, 2, 3, 4].map(i => (
                                        <div key={i} className="space-y-2">
                                            <Skeleton className="h-4 w-24" />
                                            <Skeleton className="h-12 w-full" />
                                        </div>
                                    ))}
                                </div>
                            ) : profile ? (
                                <>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                        <div className="space-y-2">
                                            <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">First Name</label>
                                            {isEditing ? (
                                                <Input value={editFirstname} onChange={e => setEditFirstname(e.target.value)} className="h-12 border-border" />
                                            ) : (
                                                <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">{profile.firstname || "—"}</div>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Last Name</label>
                                            {isEditing ? (
                                                <Input value={editLastname} onChange={e => setEditLastname(e.target.value)} className="h-12 border-border" />
                                            ) : (
                                                <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">{profile.lastname || "—"}</div>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Email</label>
                                            <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">{profile.email}</div>
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Role</label>
                                            <div className="h-12 border border-border px-4 flex items-center bg-secondary/10">
                                                <Badge variant="outline" className="uppercase text-xs tracking-wider">{profile.role || "USER"}</Badge>
                                            </div>
                                        </div>
                                    </div>
                                    {isEditing ? (
                                        <div className="flex gap-3">
                                            <Button
                                                className="rounded-none tracking-widest uppercase font-bold px-8 h-12 bg-foreground text-background"
                                                onClick={handleSaveEdit}
                                                disabled={updateProfileMutation.isPending}
                                            >
                                                {updateProfileMutation.isPending ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                                                Save Changes
                                            </Button>
                                            <Button variant="outline" className="rounded-none tracking-widest uppercase font-bold px-8 h-12" onClick={() => setIsEditing(false)}>
                                                <X className="mr-2 h-4 w-4" /> Cancel
                                            </Button>
                                        </div>
                                    ) : (
                                        <Button
                                            className="rounded-none tracking-widest uppercase font-bold px-8 h-12 bg-foreground text-background"
                                            onClick={handleStartEdit}
                                        >
                                            <Edit2 className="mr-2 h-4 w-4" /> Edit Profile
                                        </Button>
                                    )}
                                </>
                            ) : (
                                <div className="border border-border flex items-center justify-center py-16 bg-secondary/5 text-muted-foreground">
                                    Unable to load profile. Please <Link href="/login" className="underline text-primary">sign in</Link>.
                                </div>
                            )}
                        </TabsContent>

                        {/* AI Style Profile Tab */}
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

                        {/* Orders Tab */}
                        <TabsContent value="orders" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
                            <div className="flex items-center justify-between">
                                <div className="space-y-1">
                                    <h2 className="text-2xl font-bold uppercase tracking-wider">Order History</h2>
                                    <p className="text-muted-foreground font-light">View and track your recent purchases.</p>
                                </div>
                                <Button variant="outline" className="rounded-none tracking-widest uppercase text-xs font-bold" asChild>
                                    <Link href="/account/orders">View All</Link>
                                </Button>
                            </div>

                            {isOrdersLoading ? (
                                <div className="space-y-4">
                                    {[1, 2, 3].map(i => <Skeleton key={i} className="h-20 w-full" />)}
                                </div>
                            ) : orders && orders.length > 0 ? (
                                <div className="space-y-4">
                                    {orders.slice(0, 5).map((order) => (
                                        <Link key={order.id} href={`/account/orders/${order.id}`} className="block group">
                                            <div className="border border-border p-5 flex items-center justify-between hover:border-foreground/20 transition-all bg-secondary/5 hover:bg-secondary/10">
                                                <div className="space-y-1">
                                                    <div className="flex items-center gap-3">
                                                        <span className="font-bold text-sm">#{order.id.substring(0, 8)}</span>
                                                        <Badge className={`text-[10px] uppercase ${getStatusColor(order.status)}`}>{order.status}</Badge>
                                                    </div>
                                                    <p className="text-xs text-muted-foreground">
                                                        {new Date(order.createdAt).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                                                        {order.items && ` · ${order.items.length} item${order.items.length > 1 ? "s" : ""}`}
                                                    </p>
                                                </div>
                                                <span className="font-bold text-lg">${order.totalAmount?.toFixed(2)}</span>
                                            </div>
                                        </Link>
                                    ))}
                                </div>
                            ) : (
                                <div className="border border-border flex flex-col items-center justify-center py-24 bg-secondary/5 text-muted-foreground space-y-4">
                                    <Package className="h-10 w-10 text-muted-foreground/30" />
                                    <p>No orders yet.</p>
                                    <Button variant="outline" asChild><Link href="/shop">Start Shopping</Link></Button>
                                </div>
                            )}
                        </TabsContent>

                        {/* Wishlist Tab */}
                        <TabsContent value="wishlist" className="m-0 space-y-8 animate-in fade-in slide-in-from-right-4 duration-500">
                            <div className="flex items-center justify-between">
                                <div className="space-y-1">
                                    <h2 className="text-2xl font-bold uppercase tracking-wider">Your Wishlist</h2>
                                    <p className="text-muted-foreground font-light">Items you&apos;ve saved for later.</p>
                                </div>
                                {wishlist && wishlist.length > 0 && (
                                    <Button variant="outline" className="rounded-none tracking-widest uppercase text-xs font-bold" asChild>
                                        <Link href="/account/wishlist">View All</Link>
                                    </Button>
                                )}
                            </div>

                            {isWishlistLoading ? (
                                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                    {[1, 2, 3, 4].map(i => <Skeleton key={i} className="aspect-[3/4] w-full" />)}
                                </div>
                            ) : wishlist && wishlist.length > 0 ? (
                                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                    {wishlist.slice(0, 8).map((item) => (
                                        <Link key={item.id} href={`/product/${item.product.id}`} className="group block">
                                            <div className="aspect-[3/4] bg-secondary/20 mb-3 overflow-hidden relative border border-border/50">
                                                <div className="absolute inset-0 bg-stone-100 dark:bg-stone-900 flex items-center justify-center transition-transform duration-700 group-hover:scale-105 text-muted-foreground/30 text-xs uppercase tracking-widest">
                                                    {item.product.categoryName}
                                                </div>
                                            </div>
                                            <h3 className="text-sm font-bold uppercase tracking-wider mb-1 line-clamp-1 group-hover:underline underline-offset-4">
                                                {item.product.name}
                                            </h3>
                                            <p className="text-sm text-muted-foreground">${item.product.basePrice?.toLocaleString()}</p>
                                        </Link>
                                    ))}
                                </div>
                            ) : (
                                <div className="border border-border flex flex-col items-center justify-center py-24 bg-secondary/5 text-muted-foreground space-y-4">
                                    <Heart className="h-10 w-10 text-muted-foreground/30" />
                                    <p>Your wishlist is currently empty.</p>
                                    <Button variant="outline" asChild><Link href="/shop">Explore Products</Link></Button>
                                </div>
                            )}
                        </TabsContent>
                    </div>
                </Tabs>
            </div>
        </div>
    );
}
