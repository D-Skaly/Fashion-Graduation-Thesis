import { describe, it, expect } from 'vitest';
import { sanitizeHtml, sanitizeText, validateImageFile } from '../lib/sanitize';

describe('sanitizeHtml', () => {
  it('should remove script tags', () => {
    const input = '<script>alert("XSS")</script><p>Hello</p>';
    const result = sanitizeHtml(input);
    expect(result).toBe('<p>Hello</p>');
  });

  it('should allow safe tags', () => {
    const input = '<p>Hello <strong>World</strong></p>';
    const result = sanitizeHtml(input);
    expect(result).toContain('<p>');
    expect(result).toContain('<strong>');
  });

  it('should remove dangerous attributes', () => {
    const input = '<a href="https://example.com" onclick="alert(1)">Link</a>';
    const result = sanitizeHtml(input);
    expect(result).not.toContain('onclick');
    expect(result).toContain('href');
  });

  it('should allow safe URL schemes', () => {
    const input = '<a href="https://example.com">Link</a>';
    const result = sanitizeHtml(input);
    expect(result).toContain('href="https://example.com"');
  });
});
