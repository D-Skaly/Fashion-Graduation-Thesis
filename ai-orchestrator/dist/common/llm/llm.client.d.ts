/**
 * Thin abstraction around an LLM provider.
 *
 * This keeps provider-specific details isolated so both stylist/strategist services
 * can focus on prompt design and post-processing.
 */
export declare class LlmClient {
    private readonly logger;
    complete(prompt: string): Promise<string>;
}
