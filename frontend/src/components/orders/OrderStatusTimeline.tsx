"use client";

import { format } from "date-fns";
import { 
  CheckCircle2, 
  Clock, 
  Package, 
  Truck, 
  Home, 
  XCircle,
  AlertCircle,
  FileText
} from "lucide-react";

interface OrderStatusHistory {
  id: string;
  status: string;
  note: string;
  createdAt: string;
}

interface OrderStatusTimelineProps {
  history: OrderStatusHistory[];
}

const statusConfig: Record<string, { 
  icon: React.ElementType; 
  label: string; 
  color: string;
  bgColor: string;
}> = {
  PENDING: {
    icon: Clock,
    label: "Order Placed",
    color: "text-yellow-600",
    bgColor: "bg-yellow-100",
  },
  CONFIRMED: {
    icon: FileText,
    label: "Order Confirmed",
    color: "text-blue-600",
    bgColor: "bg-blue-100",
  },
  PROCESSING: {
    icon: Package,
    label: "Processing",
    color: "text-purple-600",
    bgColor: "bg-purple-100",
  },
  SHIPPED: {
    icon: Truck,
    label: "Shipped",
    color: "text-indigo-600",
    bgColor: "bg-indigo-100",
  },
  OUT_FOR_DELIVERY: {
    icon: Truck,
    label: "Out for Delivery",
    color: "text-orange-600",
    bgColor: "bg-orange-100",
  },
  DELIVERED: {
    icon: Home,
    label: "Delivered",
    color: "text-green-600",
    bgColor: "bg-green-100",
  },
  CANCELLED: {
    icon: XCircle,
    label: "Cancelled",
    color: "text-red-600",
    bgColor: "bg-red-100",
  },
  REFUNDED: {
    icon: AlertCircle,
    label: "Refunded",
    color: "text-gray-600",
    bgColor: "bg-gray-100",
  },
};

export function OrderStatusTimeline({ history }: OrderStatusTimelineProps) {
  if (!history || history.length === 0) {
    return (
      <div className="text-center py-8 text-muted-foreground">
        <Clock className="h-12 w-12 mx-auto mb-3 opacity-50" />
        <p>No status history available</p>
      </div>
    );
  }

  // Sort history by createdAt (oldest first)
  const sortedHistory = [...history].sort(
    (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  );

  return (
    <div className="relative">
      {/* Timeline line */}
      <div className="absolute left-5 top-0 bottom-0 w-0.5 bg-border" />

      <div className="space-y-6">
        {sortedHistory.map((item, index) => {
          const config = statusConfig[item.status] || {
            icon: AlertCircle,
            label: item.status,
            color: "text-gray-600",
            bgColor: "bg-gray-100",
          };
          const Icon = config.icon;
          const isLast = index === sortedHistory.length - 1;

          return (
            <div key={item.id} className="relative flex gap-4">
              {/* Icon */}
              <div
                className={`relative z-10 flex-shrink-0 w-10 h-10 rounded-full ${config.bgColor} flex items-center justify-center`}
              >
                <Icon className={`h-5 w-5 ${config.color}`} />
              </div>

              {/* Content */}
              <div className="flex-1 pt-1">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">
                  <h4 className="font-semibold text-sm">{config.label}</h4>
                  <span className="text-xs text-muted-foreground">
                    {format(new Date(item.createdAt), "MMM dd, yyyy 'at' h:mm a")}
                  </span>
                </div>
                {item.note && (
                  <p className="text-sm text-muted-foreground mt-1">{item.note}</p>
                )}
                {isLast && (
                  <span className="inline-block mt-2 text-xs font-medium px-2 py-0.5 rounded bg-primary/10 text-primary">
                    Current Status
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
