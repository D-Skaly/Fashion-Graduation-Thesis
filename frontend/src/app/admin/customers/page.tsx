"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    Table, TableBody, TableCell,
    TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
    DropdownMenu, DropdownMenuContent,
    DropdownMenuItem, DropdownMenuLabel,
    DropdownMenuSeparator, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { MoreHorizontal, RotateCw, Mail, Calendar, UserX, UserCheck } from "lucide-react";
import api from "@/lib/axios";
import { toast } from "sonner";
import { Input } from "@/components/ui/input";
import { useState } from "react";

interface Customer {
    id: string;
    firstname: string;
    lastname: string;
    email: string;
    role: string;
    isActive: boolean;
    createdAt: string;
}

interface CustomerPage {
    content: Customer[];
    totalElements: number;
}

const fetchCustomers = async (): Promise<Customer[]> => {
    const { data } = await api.get<CustomerPage>("/users?size=100");
    if (data && 'content' in data) return data.content;
    return Array.isArray(data) ? data : [];
};

export default function CustomersPage() {
    const [search, setSearch] = useState("");
    const queryClient = useQueryClient();
    const { data: customers, isLoading, isError, refetch } = useQuery({
        queryKey: ["admin-customers"],
        queryFn: fetchCustomers,
    });

    const banMutation = useMutation({
        mutationFn: async ({ id, active }: { id: string, active: boolean }) => {
            await api.put(`/users/${id}/status`, { active });
        },
        onSuccess: (_, variables) => {
            toast.success(variables.active ? "Access restored" : "Account suspended");
            queryClient.invalidateQueries({ queryKey: ["admin-customers"] });
        },
        onError: () => toast.error("Security protocol failed"),
    });

    const filtered = customers?.filter(c => 
        `${c.firstname} ${c.lastname}`.toLowerCase().includes(search.toLowerCase()) ||
        c.email.toLowerCase().includes(search.toLowerCase())
    );

    if (isError) {
        return (
            <div className="flex flex-col items-center justify-center h-96 gap-6">
                <div className="bg-red-500/10 p-4 rounded-full"><RotateCw className="h-8 w-8 text-red-400" /></div>
                <div className="text-center">
                    <p className="text-zinc-100 font-black uppercase tracking-widest">Network Interruption</p>
                    <p className="text-zinc-500 text-sm font-medium mt-1">Unable to access user registry.</p>
                </div>
                <Button variant="outline" onClick={() => refetch()} className="gap-2 border-zinc-800 rounded-xl px-8 h-12 text-xs font-bold uppercase tracking-widest">Retry Sync</Button>
            </div>
        );
    }

    return (
        <div className="w-full space-y-8">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight uppercase">Population Registry</h1>
                    <p className="text-zinc-500 text-xs font-bold uppercase tracking-[0.2em] mt-1">
                        {customers ? `${customers.length} REGISTERED GENOMES` : "INITIALIZING CORE..."}
                    </p>
                </div>
            </div>

            <div className="relative max-w-md">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-zinc-500" />
                <Input
                    placeholder="Search by name or email..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="pl-12 h-12 bg-zinc-900 border-zinc-800 text-white placeholder:text-zinc-600 rounded-xl focus:border-primary/50"
                />
            </div>

            <div className="rounded-2xl border border-zinc-800 bg-zinc-900/50 overflow-hidden shadow-xl">
                <Table>
                    <TableHeader className="bg-zinc-900/50">
                        <TableRow className="border-zinc-800 hover:bg-transparent">
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 pl-6">Subject</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Authorization</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Stability</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Joined</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 text-right pr-6">Management</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            Array.from({ length: 6 }).map((_, i) => (
                                <TableRow key={i} className="border-zinc-800">
                                    <TableCell className="py-5 pl-6"><Skeleton className="h-12 w-12 rounded-full bg-zinc-800 inline-block mr-4" /><Skeleton className="h-4 w-32 bg-zinc-800 inline-block align-middle" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-5 w-20 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-5 w-20 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-4 w-24 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5 pr-6"><Skeleton className="h-9 w-9 ml-auto bg-zinc-800" /></TableCell>
                                </TableRow>
                            ))
                        ) : filtered?.map((customer) => {
                            const initials = `${customer.firstname?.[0] || ""}${customer.lastname?.[0] || ""}`.toUpperCase();
                            return (
                                <TableRow key={customer.id} className="border-zinc-800 hover:bg-zinc-800/30 transition-colors group">
                                    <TableCell className="py-5 pl-6">
                                        <div className="flex items-center gap-4">
                                            <Avatar className="h-10 w-10 border border-zinc-800">
                                                <AvatarFallback className="bg-zinc-800 text-zinc-400 text-xs font-black">{initials}</AvatarFallback>
                                            </Avatar>
                                            <div className="flex flex-col">
                                                <span className="font-bold text-zinc-100 text-sm group-hover:text-primary transition-colors">{customer.firstname} {customer.lastname}</span>
                                                <span className="text-[10px] text-zinc-500 font-bold tracking-wider">{customer.email}</span>
                                            </div>
                                        </div>
                                    </TableCell>
                                    <TableCell className="py-5">
                                        <Badge className={`bg-zinc-900 border border-zinc-800 text-[10px] font-black uppercase tracking-widest px-2.5 py-0.5 ${customer.role.includes('ADMIN') ? 'text-primary' : 'text-zinc-400'}`}>
                                            {customer.role}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="py-5">
                                        {customer.isActive ? (
                                            <Badge className="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px] font-black uppercase tracking-widest px-2.5 py-0.5">Stable</Badge>
                                        ) : (
                                            <Badge className="bg-red-500/10 text-red-400 border border-red-500/20 text-[10px] font-black uppercase tracking-widest px-2.5 py-0.5">Suspended</Badge>
                                        )}
                                    </TableCell>
                                    <TableCell className="py-5 text-zinc-500 text-xs font-bold uppercase tracking-widest">
                                        {new Date(customer.createdAt).toLocaleDateString()}
                                    </TableCell>
                                    <TableCell className="py-5 text-right pr-6">
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant="ghost" className="h-9 w-9 p-0 hover:bg-zinc-800 rounded-xl border border-transparent hover:border-zinc-700 transition-all">
                                                    <MoreHorizontal className="h-4 w-4 text-zinc-400" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end" className="w-56 bg-zinc-900 border-zinc-800 text-zinc-100 rounded-xl p-1 shadow-2xl">
                                                <DropdownMenuLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500 px-3 py-2">Subject Management</DropdownMenuLabel>
                                                <DropdownMenuSeparator className="bg-zinc-800" />
                                                <DropdownMenuItem className="rounded-lg focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                                    <Mail className="mr-3 h-4 w-4 text-zinc-500" />
                                                    <span className="text-xs font-bold uppercase tracking-wider">Direct Message</span>
                                                </DropdownMenuItem>
                                                <DropdownMenuItem className="rounded-lg focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                                    <Calendar className="mr-3 h-4 w-4 text-zinc-500" />
                                                    <span className="text-xs font-bold uppercase tracking-wider">Activity Log</span>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator className="bg-zinc-800" />
                                                <DropdownMenuItem
                                                    className={`rounded-lg focus:bg-zinc-800 cursor-pointer py-2.5 ${customer.isActive ? 'text-red-400 focus:text-red-400 focus:bg-red-500/10' : 'text-emerald-400 focus:text-emerald-400 focus:bg-emerald-500/10'}`}
                                                    onClick={() => banMutation.mutate({ id: customer.id, active: !customer.isActive })}
                                                >
                                                    {customer.isActive ? <UserX className="mr-3 h-4 w-4" /> : <UserCheck className="mr-3 h-4 w-4" />}
                                                    <span className="text-xs font-bold uppercase tracking-wider">{customer.isActive ? 'Suspend Access' : 'Restore Access'}</span>
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
