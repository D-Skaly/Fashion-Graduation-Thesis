"use client";

import { useState, useEffect } from "react";
import { cn } from "@/lib/utils";

interface CountdownTimerProps {
  targetDate: Date | string;
  onExpire?: () => void;
  className?: string;
  showLabels?: boolean;
  size?: "sm" | "md" | "lg";
}

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

function calculateTimeLeft(target: Date): TimeLeft {
  const difference = target.getTime() - new Date().getTime();
  
  if (difference <= 0) {
    return { days: 0, hours: 0, minutes: 0, seconds: 0 };
  }

  return {
    days: Math.floor(difference / (1000 * 60 * 60 * 24)),
    hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
    minutes: Math.floor((difference / 1000 / 60) % 60),
    seconds: Math.floor((difference / 1000) % 60),
  };
}

function padZero(num: number): string {
  return num.toString().padStart(2, "0");
}

export function CountdownTimer({
  targetDate,
  onExpire,
  className,
  showLabels = true,
  size = "md",
}: CountdownTimerProps) {
  const target = typeof targetDate === "string" ? new Date(targetDate) : targetDate;
  const [timeLeft, setTimeLeft] = useState<TimeLeft>(() => calculateTimeLeft(target));
  const [hasExpired, setHasExpired] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      const newTimeLeft = calculateTimeLeft(target);
      setTimeLeft(newTimeLeft);

      if (
        newTimeLeft.days === 0 &&
        newTimeLeft.hours === 0 &&
        newTimeLeft.minutes === 0 &&
        newTimeLeft.seconds === 0
      ) {
        setHasExpired(true);
        onExpire?.();
        clearInterval(timer);
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [target, onExpire]);

  const sizeClasses = {
    sm: { number: "text-lg", label: "text-[10px]" },
    md: { number: "text-2xl", label: "text-xs" },
    lg: { number: "text-4xl", label: "text-sm" },
  };

  if (hasExpired) {
    return (
      <div className={cn("text-center text-muted-foreground", className)}>
        <p className="text-sm">Offer has expired</p>
      </div>
    );
  }

  const timeUnits = [
    { label: "Days", value: timeLeft.days },
    { label: "Hours", value: timeLeft.hours },
    { label: "Min", value: timeLeft.minutes },
    { label: "Sec", value: timeLeft.seconds },
  ];

  return (
    <div className={cn("flex items-center gap-2", className)}>
      {timeUnits.map((unit, index) => (
        <div key={unit.label} className="flex flex-col items-center">
          <div
            className={cn(
              "font-bold tabular-nums",
              sizeClasses[size].number
            )}
          >
            {unit.label === "Days" ? unit.value : padZero(unit.value)}
          </div>
          {showLabels && (
            <div className={cn("text-muted-foreground uppercase tracking-wider", sizeClasses[size].label)}>
              {unit.label}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
