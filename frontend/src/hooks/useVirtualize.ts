import { useEffect, useRef } from 'react';

interface VirtualizeOptions {
  itemHeight: number;
  overScan?: number;
}

export function useVirtualize<T>(
  items: T[],
  options: VirtualizeOptions
) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [visibleRange, setVisibleRange] = useState({ start: 0, end: 20 });

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const handleScroll = () => {
      const scrollTop = container.scrollTop;
      const startIndex = Math.floor(scrollTop / options.itemHeight);
      const visibleCount = Math.ceil(container.clientHeight / options.itemHeight);
      const overScan = options.overScan || 5;
      
      setVisibleRange({
        start: Math.max(0, startIndex - overScan),
        end: Math.min(items.length, startIndex + visibleCount + overScan),
      });
    };

    container.addEventListener('scroll', handleScroll);
    return () => container.removeEventListener('scroll', handleScroll);
  }, [items.length, options.itemHeight, options.overScan]);

  const visibleItems = items.slice(visibleRange.start, visibleRange.end);
  const totalHeight = items.length * options.itemHeight;

  return {
    containerRef,
    visibleItems,
    totalHeight,
    offsetY: visibleRange.start * options.itemHeight,
    visibleRange,
  };
}
