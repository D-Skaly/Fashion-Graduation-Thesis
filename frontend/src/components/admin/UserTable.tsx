"use client";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { MoreHorizontal } from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

interface User {
    id: string;
    email: string;
    fullName: string;
    role: string;
    isBanned: boolean;
    createdAt: string;
}

interface UserTableProps {
    users: User[];
    onView?: (userId: string) => void;
    onBan?: (userId: string) => void;
    onUnban?: (userId: string) => void;
}

export function UserTable({ users, onView, onBan, onUnban }: UserTableProps) {
    return (
        <div className="rounded-md border bg-card">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Name</TableHead>
                        <TableHead>Email</TableHead>
                        <TableHead>Role</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead className="text-right">Joined</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {users.map((user) => (
                        <TableRow key={user.id}>
                            <TableCell className="font-medium">{user.fullName}</TableCell>
                            <TableCell>{user.email}</TableCell>
                            <TableCell>
                                <Badge variant="outline">{user.role}</Badge>
                            </TableCell>
                            <TableCell>
                                {user.isBanned ? (
                                    <Badge variant="destructive">Banned</Badge>
                                ) : (
                                    <Badge variant="default">Active</Badge>
                                )}
                            </TableCell>
                            <TableCell className="text-right text-sm text-muted-foreground">
                                {new Date(user.createdAt).toLocaleDateString()}
                            </TableCell>
                            <TableCell className="text-right">
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" size="icon">
                                            <MoreHorizontal className="h-4 w-4" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        <DropdownMenuItem onClick={() => onView?.(user.id)}>
                                            View Details
                                        </DropdownMenuItem>
                                        <DropdownMenuItem>Send Email</DropdownMenuItem>
                                        {user.isBanned ? (
                                            <DropdownMenuItem onClick={() => onUnban?.(user.id)}>
                                                Unban User
                                            </DropdownMenuItem>
                                        ) : (
                                            <DropdownMenuItem 
                                                className="text-red-600" 
                                                onClick={() => onBan?.(user.id)}
                                            >
                                                Ban User
                                            </DropdownMenuItem>
                                        )}
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
}
