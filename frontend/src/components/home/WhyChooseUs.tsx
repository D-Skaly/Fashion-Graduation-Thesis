"use client";

import { useEffect, useRef, useState } from "react";
import { ShieldCheck, Truck, Clock, BrainCircuit } from "lucide-react";
import { motion } from "framer-motion";

function useCountUp(end: number, duration: number = 2000, startOnView: boolean = true) {
    const [count, setCount] = useState(0);
    const [hasStarted, setHasStarted] = useState(!startOnView);
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!hasStarted) return;
        let startTime: number;
        let animationFrame: number;

        const animate = (timestamp: number) => {
            if (!startTime) startTime = timestamp;
            const progress = Math.min((timestamp - startTime) / duration, 1);
            // Ease out cubic
            const easeOut = 1 - Math.pow(1 - progress, 3);
            setCount(Math.floor(easeOut * end));

            if (progress < 1) {
                animationFrame = requestAnimationFrame(animate);
            }
        };

        animationFrame = requestAnimationFrame(animate);
        return () => cancelAnimationFrame(animationFrame);
    }, [hasStarted, end, duration]);

    return { count, ref, setHasStarted };
}

function CountUpStat({ value, suffix = "", label }: { value: number; suffix?: string; label: string }) {
    const { count, ref, setHasStarted } = useCountUp(value);

    return (
        <motion.div
            ref={ref}
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            onViewportEnter={() => setHasStarted(true)}
            className="text-center"
        >
            <div className="text-4xl md:text-5xl font-black tracking-tight mb-2">
                {count}{suffix}
            </div>
            <p className="text-sm text-muted-foreground uppercase tracking-widest">{label}</p>
        </motion.div>
    );
}

export function WhyChooseUs() {
    return (
        <section className="relative py-24 overflow-hidden bg-secondary/30 dark:bg-background">
            {/* Background Details */}
            <div className="absolute inset-0 z-0">
                <div className="absolute -top-24 -left-24 w-96 h-96 bg-primary/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 right-0 w-[500px] h-[500px] bg-secondary/50 rounded-full blur-3xl" />
            </div>

            <div className="container relative z-10 mx-auto px-4 text-center space-y-16">
                <motion.div
                    initial={{ opacity: 0, y: 30 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true }}
                    transition={{ duration: 0.8 }}
                    className="space-y-4"
                >
                    <span className="text-sm font-semibold tracking-widest text-muted-foreground uppercase">The AI Fashion Standard</span>
                    <h2 className="text-4xl md:text-5xl font-black tracking-tight uppercase text-balance">Why Choose Us?</h2>
                </motion.div>

                {/* Stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-8 max-w-4xl mx-auto py-8">
                    <CountUpStat value={50000} suffix="+" label="Happy Customers" />
                    <CountUpStat value={98} suffix="%" label="AI Match Rate" />
                    <CountUpStat value={24} suffix="/7" label="Support" />
                    <CountUpStat value={30} suffix="" label="Day Returns" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 max-w-6xl mx-auto">
                    {[
                        { icon: ShieldCheck, title: "Quality Guarantee", desc: "We ensure every piece meets our strict quality standards before shipping." },
                        { icon: Truck, title: "Fast Shipping", desc: "Express delivery options available for all orders worldwide." },
                        { icon: Clock, title: "24/7 Support", desc: "Our dedicated support team is always ready to assist you." },
                        { icon: BrainCircuit, title: "AI Powered", desc: "Smart recommendations and virtual try-on powered by cutting-edge AI." },
                    ].map((feature, idx) => (
                        <motion.div
                            key={idx}
                            initial={{ opacity: 0, y: 50 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.8, delay: idx * 0.1 }}
                            className="p-8 bg-background/50 backdrop-blur-md rounded-3xl shadow-sm border border-border/50 hover:shadow-md hover:-translate-y-1 transition-all duration-300"
                        >
                            <div className="h-16 w-16 bg-primary/10 text-primary rounded-2xl flex items-center justify-center mx-auto mb-6 rotate-3">
                                <feature.icon className="w-8 h-8" />
                            </div>
                            <h3 className="font-bold text-xl mb-3 tracking-wide">{feature.title}</h3>
                            <p className="text-muted-foreground font-light leading-relaxed">{feature.desc}</p>
                        </motion.div>
                    ))}
                </div>
            </div>
        </section>
    );
}
