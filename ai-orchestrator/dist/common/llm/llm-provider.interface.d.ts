export interface LlmProvider {
    complete(prompt: string): Promise<string>;
}
export declare const LLM_PROVIDER = "LLM_PROVIDER";
