"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Plus, Pencil, Trash2, Check, Loader2, MapPin } from "lucide-react";
import { toast } from "sonner";
import { useRouter } from "next/navigation";
import Link from "next/link";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Alert, AlertDescription } from "@/components/ui/alert";
import api from "@/lib/axios";

const addressSchema = z.object({
  fullName: z.string().min(2, "Name is required"),
  phone: z.string().min(10, "Valid phone number required"),
  address: z.string().min(10, "Address is required"),
<<<<<<< ours
  isDefault: z.boolean().default(false),
=======
  isDefault: z.boolean(),
>>>>>>> theirs
});

type AddressFormValues = z.infer<typeof addressSchema>;

interface Address {
  id: string;
  fullName: string;
  phone: string;
  address: string;
  isDefault: boolean;
}

const fetchAddresses = async (): Promise<Address[]> => {
  const { data } = await api.get("/users/addresses");
  return Array.isArray(data) ? data : [];
};

export default function AddressBookPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const { data: addresses, isLoading } = useQuery({
    queryKey: ["addresses"],
    queryFn: fetchAddresses,
  });

  const form = useForm<AddressFormValues>({
    resolver: zodResolver(addressSchema),
    defaultValues: {
      fullName: "",
      phone: "",
      address: "",
      isDefault: false,
    },
  });

  const createMutation = useMutation({
    mutationFn: async (values: AddressFormValues) => {
      const { data } = await api.post("/users/addresses", values);
      return data;
    },
    onSuccess: () => {
      toast.success("Address added successfully");
      queryClient.invalidateQueries({ queryKey: ["addresses"] });
      setIsDialogOpen(false);
      form.reset();
    },
    onError: () => toast.error("Failed to add address"),
  });

  const updateMutation = useMutation({
    mutationFn: async ({ id, values }: { id: string; values: AddressFormValues }) => {
      const { data } = await api.put(`/users/addresses/${id}`, values);
      return data;
    },
    onSuccess: () => {
      toast.success("Address updated successfully");
      queryClient.invalidateQueries({ queryKey: ["addresses"] });
      setIsDialogOpen(false);
      setEditingAddress(null);
      form.reset();
    },
    onError: () => toast.error("Failed to update address"),
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/users/addresses/${id}`);
    },
    onSuccess: () => {
      toast.success("Address deleted");
      queryClient.invalidateQueries({ queryKey: ["addresses"] });
    },
    onError: () => toast.error("Failed to delete address"),
  });

  const setDefaultMutation = useMutation({
    mutationFn: async (id: string) => {
      await api.put(`/users/addresses/${id}/default`);
    },
    onSuccess: () => {
      toast.success("Default address updated");
      queryClient.invalidateQueries({ queryKey: ["addresses"] });
    },
    onError: () => toast.error("Failed to set default address"),
  });

  const onSubmit = (values: AddressFormValues) => {
    if (editingAddress) {
      updateMutation.mutate({ id: editingAddress.id, values });
    } else {
      createMutation.mutate(values);
    }
  };

  const handleEdit = (address: Address) => {
    setEditingAddress(address);
    form.reset({
      fullName: address.fullName,
      phone: address.phone,
      address: address.address,
      isDefault: address.isDefault,
    });
    setIsDialogOpen(true);
  };

  const handleAddNew = () => {
    setEditingAddress(null);
    form.reset();
    setIsDialogOpen(true);
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-4xl">
        <div className="h-8 w-48 bg-muted animate-pulse rounded mb-6" />
        <div className="space-y-4">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="h-32 bg-muted animate-pulse rounded-xl" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold">Address Book</h1>
          <p className="text-sm text-muted-foreground">
            Manage your saved addresses
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={handleAddNew}>
              <Plus className="h-4 w-4 mr-2" />
              Add Address
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>
                {editingAddress ? "Edit Address" : "Add New Address"}
              </DialogTitle>
            </DialogHeader>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                  control={form.control}
                  name="fullName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Full Name</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="phone"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Phone</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="address"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Address</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <div className="flex justify-end gap-2">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setIsDialogOpen(false)}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    disabled={createMutation.isPending || updateMutation.isPending}
                  >
                    {(createMutation.isPending || updateMutation.isPending) ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : editingAddress ? (
                      "Save Changes"
                    ) : (
                      "Add Address"
                    )}
                  </Button>
                </div>
              </form>
            </Form>
          </DialogContent>
        </Dialog>
      </div>

      {addresses && addresses.length > 0 ? (
        <div className="space-y-4">
          {addresses.map((address) => (
            <Card key={address.id}>
              <CardContent className="pt-6">
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold">{address.fullName}</h3>
                      {address.isDefault && (
                        <Badge variant="secondary">Default</Badge>
                      )}
                    </div>
                    <p className="text-sm text-muted-foreground">{address.phone}</p>
                    <p className="text-sm">{address.address}</p>
                  </div>
                  <div className="flex gap-2">
                    {!address.isDefault && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setDefaultMutation.mutate(address.id)}
                        disabled={setDefaultMutation.isPending}
                      >
                        <Check className="h-4 w-4" />
                      </Button>
                    )}
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleEdit(address)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => deleteMutation.mutate(address.id)}
                      disabled={deleteMutation.isPending}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <MapPin className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
            <p className="text-muted-foreground">No addresses saved yet</p>
            <Button onClick={handleAddNew} className="mt-4">
              Add Your First Address
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
