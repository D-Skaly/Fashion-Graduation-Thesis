"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, CheckCircle2, XCircle, Image as ImageIcon, Filter } from "lucide-react";
import { toast } from "sonner";
import Image from "next/image";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface Review {
  id: string;
  productId: string;
  productName: string;
  userId: string;
  userName: string;
  rating: number;
  comment: string;
  images?: string[];
  status: "PENDING" | "APPROVED" | "REJECTED";
  createdAt: string;
}

const fetchReviews = async (status?: string, rating?: string): Promise<Review[]> => {
  const params = new URLSearchParams();
  if (status && status !== "ALL") params.append("status", status);
  if (rating && rating !== "ALL") params.append("rating", rating);

  const { data } = await api.get(`/admin/reviews?${params.toString()}`);
  return Array.isArray(data) ? data : data.content || [];
};

const moderateReview = async ({
  id,
  status,
  reason,
}: {
  id: string;
  status: "APPROVED" | "REJECTED";
  reason?: string;
}) => {
  const { data } = await api.put(`/admin/reviews/${id}/moderate`, {
    status,
    reason,
  });
  return data;
};

export default function ReviewModerationPage() {
  const queryClient = useQueryClient();
  const [statusFilter, setStatusFilter] = useState("PENDING"); // Default to pending
  const [ratingFilter, setRatingFilter] = useState("ALL");
  const [selectedReview, setSelectedReview] = useState<Review | null>(null);
  const [rejectReason, setRejectReason] = useState("");

  const { data: reviews, isLoading } = useQuery({
    queryKey: ["admin-reviews", statusFilter, ratingFilter],
    queryFn: () => fetchReviews(statusFilter, ratingFilter),
  });

  const mutation = useMutation({
    mutationFn: moderateReview,
    onSuccess: () => {
      toast.success("Review status updated");
      queryClient.invalidateQueries({ queryKey: ["admin-reviews"] });
      setSelectedReview(null);
      setRejectReason("");
    },
    onError: (error: unknown) => {
      const axiosError = error as { response?: { data?: { message?: string } } };
      toast.error(axiosError.response?.data?.message || "Failed to update review");
    },
  });

  const handleModerate = (review: Review, status: "APPROVED" | "REJECTED") => {
    if (status === "REJECTED" && !rejectReason) {
      toast.error("Please provide a reason for rejection");
      return;
    }
    mutation.mutate({
      id: review.id,
      status,
      reason: status === "REJECTED" ? rejectReason : undefined,
    });
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "PENDING":
        return <Badge className="bg-yellow-500/20 text-yellow-300 border-yellow-500/30">Pending</Badge>;
      case "APPROVED":
        return <Badge className="bg-green-500/20 text-green-300 border-green-500/30">Approved</Badge>;
      case "REJECTED":
        return <Badge className="bg-red-500/20 text-red-300 border-red-500/30">Rejected</Badge>;
      default:
        return <Badge>{status}</Badge>;
    }
  };

  const renderStars = (rating: number) => {
    return (
      <div className="flex gap-0.5">
        {[1, 2, 3, 4, 5].map((star) => (
          <span
            key={star}
            className={`text-sm ${star <= rating ? "text-yellow-400" : "text-gray-600"}`}
          >
            ★
          </span>
        ))}
      </div>
    );
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-7xl space-y-6">
        <Skeleton className="h-8 w-64 bg-white/10" />
        <Skeleton className="h-16 w-full bg-white/10 rounded-xl" />
        {Array.from({ length: 5 }).map((_, i) => (
          <Skeleton key={i} className="h-32 w-full bg-white/10 rounded-xl" />
        ))}
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Review Moderation</h1>
        <p className="text-sm text-stone-400">
          Approve or reject customer reviews
        </p>
      </div>

      {/* Filters */}
      <Card className="bg-white/5 border-white/10 text-white">
        <CardContent className="pt-6">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="bg-white/5 border-white/20">
                  <Filter className="h-4 w-4 mr-2" />
                  <SelectValue placeholder="Filter by status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Status</SelectItem>
                  <SelectItem value="PENDING">Pending</SelectItem>
                  <SelectItem value="APPROVED">Approved</SelectItem>
                  <SelectItem value="REJECTED">Rejected</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1">
              <Select value={ratingFilter} onValueChange={setRatingFilter}>
                <SelectTrigger className="bg-white/5 border-white/20">
                  <SelectValue placeholder="Filter by rating" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Ratings</SelectItem>
                  <SelectItem value="5">5 Stars</SelectItem>
                  <SelectItem value="4">4 Stars</SelectItem>
                  <SelectItem value="3">3 Stars</SelectItem>
                  <SelectItem value="2">2 Stars</SelectItem>
                  <SelectItem value="1">1 Star</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Reviews List */}
      <div className="space-y-4">
        {reviews?.length === 0 ? (
          <Card className="bg-white/5 border-white/10 text-white">
            <CardContent className="py-12 text-center">
              <p className="text-stone-400">No reviews found</p>
            </CardContent>
          </Card>
        ) : (
          reviews?.map((review) => (
            <Card key={review.id} className="bg-white/5 border-white/10 text-white">
              <CardContent className="pt-6">
                <div className="flex flex-col sm:flex-row justify-between gap-4">
                  <div className="space-y-3 flex-1">
                    {/* Header */}
                    <div className="flex items-start justify-between">
                      <div>
                        <div className="flex items-center gap-2">
                          <h3 className="font-semibold">{review.userName}</h3>
                          {getStatusBadge(review.status)}
                        </div>
                        <p className="text-sm text-stone-400">{review.productName}</p>
                      </div>
                      {renderStars(review.rating)}
                    </div>

                    {/* Comment */}
                    <p className="text-sm text-stone-300 leading-relaxed">
                      {review.comment}
                    </p>

                    {/* Images */}
                    {review.images && review.images.length > 0 && (
                      <div className="flex gap-2 flex-wrap">
                        {review.images.map((img, idx) => (
                          <Dialog key={idx}>
                            <DialogTrigger asChild>
                              <div className="relative h-16 w-16 rounded-lg overflow-hidden border border-white/10 cursor-pointer hover:border-white/30 transition-colors">
                                <Image
                                  src={img}
                                  alt="Review"
                                  fill
                                  className="object-cover"
                                  sizes="64px"
                                />
                              </div>
                            </DialogTrigger>
                            <DialogContent className="max-w-2xl">
                              <DialogHeader>
                                <DialogTitle>Review Image</DialogTitle>
                              </DialogHeader>
                              <div className="relative aspect-square w-full">
                                <Image
                                  src={img}
                                  alt="Review"
                                  fill
                                  className="object-contain"
                                  sizes="(max-width: 768px) 100vw, 768px"
                                />
                              </div>
                            </DialogContent>
                          </Dialog>
                        ))}
                      </div>
                    )}

                    {/* Date */}
                    <p className="text-xs text-stone-500">
                      {new Date(review.createdAt).toLocaleDateString("en-US", {
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                      })}
                    </p>
                  </div>

                  {/* Actions */}
                  {review.status === "PENDING" && (
                    <div className="flex sm:flex-col gap-2 sm:w-48">
                      <Button
                        size="sm"
                        className="bg-green-600 hover:bg-green-700 text-white"
                        onClick={() => handleModerate(review, "APPROVED")}
                        disabled={mutation.isPending}
                      >
                        <CheckCircle2 className="h-4 w-4 mr-2" />
                        Approve
                      </Button>
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button
                            size="sm"
                            variant="destructive"
                            disabled={mutation.isPending}
                          >
                            <XCircle className="h-4 w-4 mr-2" />
                            Reject
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Reject Review</DialogTitle>
                          </DialogHeader>
                          <div className="space-y-4 pt-4">
                            <Textarea
                              placeholder="Reason for rejection..."
                              value={rejectReason}
                              onChange={(e) => setRejectReason(e.target.value)}
                              className="bg-white/5 border-white/20 text-white"
                            />
                            <div className="flex justify-end gap-2">
                              <Button
                                variant="outline"
                                onClick={() => setRejectReason("")}
                              >
                                Cancel
                              </Button>
                              <Button
                                variant="destructive"
                                onClick={() => handleModerate(review, "REJECTED")}
                                disabled={mutation.isPending || !rejectReason}
                              >
                                {mutation.isPending ? (
                                  <Loader2 className="h-4 w-4 animate-spin" />
                                ) : (
                                  "Confirm Reject"
                                )}
                              </Button>
                            </div>
                          </div>
                        </DialogContent>
                      </Dialog>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </div>
  );
}
