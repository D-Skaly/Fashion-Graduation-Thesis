"use client";

import { useQuery } from "@tanstack/react-query";
import { useParams } from "next/navigation";
import { ArrowLeft, Mail, Phone, Calendar, ShoppingBag } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import api from "@/lib/axios";

interface User {
    id: string;
    email: string;
    fullName: string;
    phone?: string;
    role: string;
    isBanned: boolean;
    createdAt: string;
    totalOrders?: number;
    totalSpent?: number;
}

const fetchUserDetail = async (userId: string): Promise<User> => {
    const { data } = await api.get(`/users/${userId}`);
    return data;
};

export default function UserDetailPage() {
    const params = useParams();
    const userId = params.id as string;

    const { data: user, isLoading } = useQuery({
        queryKey: ["admin-user", userId],
        queryFn: () => fetchUserDetail(userId),
        enabled: !!userId,
    });

    if (isLoading) {
        return <div className="p-8">Loading...</div>;
    }

    if (!user) {
        return <div className="p-8">User not found</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link href="/admin/customers">
                        <ArrowLeft className="h-5 w-5" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-2xl font-bold">{user.fullName}</h1>
                    <p className="text-sm text-muted-foreground">{user.email}</p>
                </div>
                <div className="ml-auto">
                    {user.isBanned ? (
                        <Badge variant="destructive">Banned</Badge>
                    ) : (
                        <Badge>Active</Badge>
                    )}
                </div>
            </div>

            <div className="grid md:grid-cols-3 gap-6">
                <div className="md:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>User Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex items-center gap-3">
                                <Mail className="h-4 w-4 text-muted-foreground" />
                                <div>
                                    <p className="text-sm text-muted-foreground">Email</p>
                                    <p className="font-medium">{user.email}</p>
                                </div>
                            </div>
                            {user.phone && (
                                <div className="flex items-center gap-3">
                                    <Phone className="h-4 w-4 text-muted-foreground" />
                                    <div>
                                        <p className="text-sm text-muted-foreground">Phone</p>
                                        <p className="font-medium">{user.phone}</p>
                                    </div>
                                </div>
                            )}
                            <div className="flex items-center gap-3">
                                <Calendar className="h-4 w-4 text-muted-foreground" />
                                <div>
                                    <p className="text-sm text-muted-foreground">Joined</p>
                                    <p className="font-medium">
                                        {new Date(user.createdAt).toLocaleDateString()}
                                    </p>
                                </div>
                            </div>
                            <div className="flex items-center gap-3">
                                <ShoppingBag className="h-4 w-4 text-muted-foreground" />
                                <div>
                                    <p className="text-sm text-muted-foreground">Role</p>
                                    <Badge variant="outline">{user.role}</Badge>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Order Statistics</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div>
                                <p className="text-sm text-muted-foreground">Total Orders</p>
                                <p className="text-2xl font-bold">{user.totalOrders || 0}</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Total Spent</p>
                                <p className="text-2xl font-bold">
                                    ${(user.totalSpent || 0).toLocaleString()}
                                </p>
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Actions</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-2">
                            <Button variant="outline" className="w-full">
                                Send Email
                            </Button>
                            <Button variant="outline" className="w-full">
                                View Order History
                            </Button>
                            {user.isBanned ? (
                                <Button variant="outline" className="w-full">
                                    Unban User
                                </Button>
                            ) : (
                                <Button variant="destructive" className="w-full">
                                    Ban User
                                </Button>
                            )}
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
