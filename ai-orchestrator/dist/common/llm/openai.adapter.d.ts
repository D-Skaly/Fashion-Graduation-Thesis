import { LlmProvider } from './llm-provider.interface';
/**
 * Adapter for OpenAI LLM provider.
 */
export declare class OpenAiAdapter implements LlmProvider {
    private readonly logger;
    complete(prompt: string): Promise<string>;
}
