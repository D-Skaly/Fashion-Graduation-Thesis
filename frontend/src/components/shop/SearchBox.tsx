"use client";

import { useState, useEffect, useRef } from "react";
import { useDebouncedCallback } from "use-debounce";
import { Search, Loader2, Clock, Trash2, TrendingUp } from "lucide-react";
import { useRouter } from "next/navigation";

import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface SearchSuggestion {
  id: string;
  name: string;
  category?: string;
}

const MAX_RECENT_SEARCHES = 5;
const POPULAR_SEARCHES = ["dress", "jacket", "sneakers", "jeans", "t-shirt"];

export function SearchBox({
  className,
  placeholder = "Search products...",
}: {
  className?: string;
  placeholder?: string;
}) {
  const router = useRouter();
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState<SearchSuggestion[]>([]);
  const [recentSearches, setRecentSearches] = useState<string[]>(() => {
    const stored = localStorage.getItem("recent-searches");
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {}
    }
    return [];
  });
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  // Save recent search
  const saveRecentSearch = (search: string) => {
    setRecentSearches((prev) => {
      const updated = [
        search,
        ...prev.filter((s) => s !== search),
      ].slice(0, MAX_RECENT_SEARCHES);
      localStorage.setItem("recent-searches", JSON.stringify(updated));
      return updated;
    });
  };

  // Clear recent searches
  const clearRecentSearches = () => {
    setRecentSearches([]);
    localStorage.removeItem("recent-searches");
  };

  // Fetch suggestions
  const fetchSuggestions = useDebouncedCallback(async (value: string) => {
    if (value.length < 2) {
      setSuggestions([]);
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch(`/api/products/search?q=${encodeURIComponent(value)}&limit=5`);
      if (response.ok) {
        const data = await response.json();
        setSuggestions(data.content || data || []);
      }
    } catch {}
    setIsLoading(false);
  }, 300);

  // Handle search
  const handleSearch = (searchQuery: string) => {
    if (!searchQuery.trim()) return;
    saveRecentSearch(searchQuery.trim());
    setIsOpen(false);
    router.push(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
  };

  // Close on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div ref={wrapperRef} className={`relative ${className || ""}`}>
      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder={placeholder}
          value={query}
          onChange={(e) => {
            const value = e.target.value;
            setQuery(value);
            fetchSuggestions(value);
            if (value.length > 0) setIsOpen(true);
          }}
          onFocus={() => query.length === 0 && setIsOpen(true)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              handleSearch(query);
            }
          }}
          className="pl-10"
        />
      </div>

      {/* Dropdown */}
      {isOpen && (
        <Card className="absolute top-full left-0 right-0 mt-2 z-50 max-h-96 overflow-auto p-2">
          {/* Suggestions */}
          {query.length >= 2 && (
            <>
              <div className="px-3 py-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                Suggestions
              </div>
              {isLoading ? (
                <div className="px-3 py-4 text-center">
                  <Loader2 className="h-4 w-4 animate-spin mx-auto text-muted-foreground" />
                </div>
              ) : suggestions.length > 0 ? (
                suggestions.map((item) => (
                  <Button
                    key={item.id}
                    variant="ghost"
                    className="w-full justify-start"
                    onClick={() => handleSearch(item.name)}
                  >
                    <Search className="mr-2 h-4 w-4 text-muted-foreground" />
                    <span className="flex-1 text-left">{item.name}</span>
                    {item.category && (
                      <span className="text-xs text-muted-foreground">{item.category}</span>
                    )}
                  </Button>
                ))
              ) : (
                <p className="px-3 py-2 text-sm text-muted-foreground">No suggestions found</p>
              )}
            </>
          )}

          {/* Recent Searches */}
          {query.length === 0 && recentSearches.length > 0 && (
            <>
              <div className="px-3 py-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider flex items-center justify-between">
                <span>Recent Searches</span>
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-auto p-0 text-xs hover:text-destructive"
                  onClick={clearRecentSearches}
                >
                  Clear
                </Button>
              </div>
              {recentSearches.map((search) => (
                <Button
                  key={search}
                  variant="ghost"
                  className="w-full justify-start"
                  onClick={() => handleSearch(search)}
                >
                  <Clock className="mr-2 h-4 w-4 text-muted-foreground" />
                  {search}
                </Button>
              ))}
            </>
          )}

          {/* Popular Searches */}
          {query.length === 0 && (
            <>
              <div className="px-3 py-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                Popular Searches
              </div>
              {POPULAR_SEARCHES.map((search) => (
                <Button
                  key={search}
                  variant="ghost"
                  className="w-full justify-start"
                  onClick={() => handleSearch(search)}
                >
                  <TrendingUp className="mr-2 h-4 w-4 text-muted-foreground" />
                  {search}
                </Button>
              ))}
            </>
          )}
        </Card>
      )}
    </div>
  );
}
