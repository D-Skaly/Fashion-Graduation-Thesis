"use client";

import { useState } from "react";
import { Star, ThumbsUp, ChevronDown, ChevronUp } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { sanitizeHtml } from "@/lib/sanitize";

interface Review {
    id: string;
    userId: string;
    userName: string;
    userAvatar?: string;
    rating: number;
    comment: string;
    images?: string[];
    isVerifiedPurchase: boolean;
    isHelpful: number;
    createdAt: string;
}

interface ReviewListProps {
    reviews: Review[];
    averageRating: number;
    totalReviews: number;
    onHelpfulVote?: (reviewId: string) => void;
}

export function ReviewList({ reviews, averageRating, totalReviews, onHelpfulVote }: ReviewListProps) {
    const [showAllReviews, setShowAllReviews] = useState(false);
    const [selectedRating, setSelectedRating] = useState<number | null>(null);

    const filteredReviews = selectedRating
        ? reviews.filter(r => r.rating === selectedRating)
        : reviews;

    const displayReviews = showAllReviews ? filteredReviews : filteredReviews.slice(0, 5);

    const ratingDistribution = [5, 4, 3, 2, 1].map(rating => ({
        rating,
        count: reviews.filter(r => r.rating === rating).length,
        percentage: (reviews.filter(r => r.rating === rating).length / totalReviews) * 100
    }));

    const renderStars = (rating: number) => {
        return Array.from({ length: 5 }).map((_, i) => (
            <Star
                key={i}
                className={`h-4 w-4 ${
                    i < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300"
                }`}
            />
        ));
    };

    return (
        <div className="space-y-8">
            {/* Rating Summary */}
            <div>
                <h2 className="text-2xl font-bold mb-6">Customer Reviews</h2>
                <div className="grid md:grid-cols-2 gap-8">
                    {/* Average Rating */}
                    <div className="flex items-center gap-4">
                        <div className="text-5xl font-bold">{averageRating.toFixed(1)}</div>
                        <div className="space-y-1">
                            <div className="flex">{renderStars(Math.round(averageRating))}</div>
                            <p className="text-sm text-muted-foreground">{totalReviews} reviews</p>
                        </div>
                    </div>

                    {/* Rating Distribution */}
                    <div className="space-y-2">
                        {ratingDistribution.map(({ rating, count, percentage }) => (
                            <div key={rating} className="flex items-center gap-2">
                                <span className="text-sm w-8">{rating} star</span>
                                <div className="flex-1 h-2 bg-secondary rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-yellow-400 transition-all"
                                        style={{ width: `${percentage}%` }}
                                    />
                                </div>
                                <span className="text-sm text-muted-foreground w-8">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            <Separator />

            {/* Filter by Rating */}
            <div className="flex flex-wrap gap-2">
                <Button
                    variant={selectedRating === null ? "default" : "outline"}
                    size="sm"
                    onClick={() => setSelectedRating(null)}
                >
                    All Reviews
                </Button>
                {[5, 4, 3, 2, 1].map(rating => (
                    <Button
                        key={rating}
                        variant={selectedRating === rating ? "default" : "outline"}
                        size="sm"
                        onClick={() => setSelectedRating(rating)}
                    >
                        {rating} Star
                    </Button>
                ))}
            </div>

            {/* Reviews List */}
            <div className="space-y-6">
                {displayReviews.length === 0 ? (
                    <p className="text-center text-muted-foreground py-8">No reviews found</p>
                ) : (
                    displayReviews.map((review) => (
                        <div key={review.id} className="space-y-3">
                            <div className="flex items-start justify-between">
                                <div className="flex items-center gap-3">
                                    <Avatar className="h-10 w-10">
                                        <AvatarFallback>
                                            {review.userName.charAt(0).toUpperCase()}
                                        </AvatarFallback>
                                    </Avatar>
                                    <div>
                                        <p className="font-medium">{review.userName}</p>
                                        <div className="flex items-center gap-2">
                                            <div className="flex">{renderStars(review.rating)}</div>
                                            {review.isVerifiedPurchase && (
                                                <Badge variant="secondary" className="text-xs">
                                                    Verified Purchase
                                                </Badge>
                                            )}
                                        </div>
                                    </div>
                                </div>
                                <span className="text-sm text-muted-foreground">
                                    {new Date(review.createdAt).toLocaleDateString()}
                                </span>
                            </div>

                            <p className="text-muted-foreground" dangerouslySetInnerHTML={{ __html: sanitizeHtml(review.comment) }} />

                            {review.images && review.images.length > 0 && (
                                <div className="flex gap-2">
                                    {review.images.map((image, index) => (
                                        <div
                                            key={index}
                                            className="w-20 h-20 bg-secondary rounded-md overflow-hidden"
                                        >
                                            {/* Placeholder for review image */}
                                            <div className="w-full h-full flex items-center justify-center text-xs text-muted-foreground">
                                                Image
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            <div className="flex items-center gap-2">
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    className="text-muted-foreground"
                                    onClick={() => onHelpfulVote?.(review.id)}
                                >
                                    <ThumbsUp className="h-4 w-4 mr-1" />
                                    Helpful ({review.isHelpful})
                                </Button>
                            </div>

                            <Separator />
                        </div>
                    ))
                )}
            </div>

            {/* Show More Button */}
            {filteredReviews.length > 5 && (
                <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => setShowAllReviews(!showAllReviews)}
                >
                    {showAllReviews ? (
                        <>
                            Show Less <ChevronUp className="ml-2 h-4 w-4" />
                        </>
                    ) : (
                        <>
                            Show All {filteredReviews.length} Reviews <ChevronDown className="ml-2 h-4 w-4" />
                        </>
                    )}
                </Button>
            )}
        </div>
    );
}
