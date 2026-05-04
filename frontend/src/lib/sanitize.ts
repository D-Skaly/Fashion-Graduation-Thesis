import DOMPurify from 'dompurify';

interface SanitizeOptions {
  allowedTags?: string[];
  allowedAttributes?: string[];
  allowedSchemes?: string[];
}

const DEFAULT_OPTIONS: DOMPurify.SanitizeConfig = {
  ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a', 'p', 'br', 'ul', 'ol', 'li', 'span'],
  ALLOWED_ATTR: ['href', 'title', 'target', 'class'],
  ALLOWED_SCHEMES: ['http', 'https'],
};

/**
 * Sanitize HTML content to prevent XSS attacks
 */
export function sanitizeHtml(html: string, options?: SanitizeOptions): string {
  if (typeof window === 'undefined') return html; // SSR safety
  
  const config = { ...DEFAULT_OPTIONS };
  if (options?.allowedTags) config.ALLOWED_TAGS = options.allowedTags;
  if (options?.allowedAttributes) config.ALLOWED_ATTR = options.allowedAttributes;
  if (options?.allowedSchemes) config.ALLOWED_SCHEMES = options.allowedSchemes;
  
  return DOMPurify.sanitize(html, config);
}

/**
 * Sanitize plain text input - removes special characters
 */
export function sanitizeText(input: string): string {
  if (!input) return input;
  
  // Remove HTML tags
  let sanitized = input.replace(/<[^>]*>/g, '');
  
  // Remove script tags and content
  sanitized = sanitized.replace(/<script.*?>.*?<\/script>/gi, '');
  
  // Trim and limit length
  return sanitized.trim().slice(0, 5000);
}

/**
 * Sanitize file name for uploads
 */
export function sanitizeFileName(fileName: string): string {
  return fileName
    .replace(/[^a-zA-Z0-9._-]/g, '_')
    .replace(/_{2,}/g, '_')
    .slice(0, 255);
}

/**
 * Validate image file before upload
 */
export function validateImageFile(file: File): { valid: boolean; error?: string } {
  // Check file type
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
  if (!allowedTypes.includes(file.type)) {
    return { valid: false, error: 'Invalid file type. Only JPEG, PNG, WebP, and GIF are allowed.' };
  }
  
  // Check file size (10MB limit)
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    return { valid: false, error: 'File too large. Maximum size is 10MB.' };
  }
  
  return { valid: true };
}
