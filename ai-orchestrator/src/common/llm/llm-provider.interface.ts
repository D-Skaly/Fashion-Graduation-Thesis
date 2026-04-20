export interface LlmProvider {
  complete(prompt: string): Promise<string>;
}

export const LLM_PROVIDER = 'LLM_PROVIDER';
