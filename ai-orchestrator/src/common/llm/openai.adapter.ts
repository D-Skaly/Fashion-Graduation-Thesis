import { Injectable, InternalServerErrorException, Logger } from '@nestjs/common';
import { LlmProvider } from './llm-provider.interface';

/**
 * Adapter for OpenAI LLM provider.
 */
@Injectable()
export class OpenAiAdapter implements LlmProvider {
  private readonly logger = new Logger(OpenAiAdapter.name);

  async complete(prompt: string): Promise<string> {
    const apiKey = process.env.OPENAI_API_KEY;
    if (!apiKey) {
      throw new InternalServerErrorException(
        'OPENAI_API_KEY is missing for orchestrator LLM calls.',
      );
    }

    const model = process.env.OPENAI_MODEL ?? 'gpt-4.1-mini';
    const url = process.env.OPENAI_BASE_URL ?? 'https://api.openai.com/v1/responses';

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${apiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        model,
        input: prompt,
      }),
    });

    if (!response.ok) {
      this.logger.error(`LLM call failed with status ${response.status}`);
      throw new InternalServerErrorException('LLM provider call failed.');
    }

    const payload = (await response.json()) as {
      output_text?: string;
      output?: Array<{ content?: Array<{ text?: string }> }>;
    };

    // Responses API returns output_text on newer versions; fallback included for compatibility.
    const text =
      payload.output_text ??
      payload.output?.flatMap((item) => item.content ?? []).map((c) => c.text ?? '').join('\n');

    if (!text || !text.trim()) {
      throw new InternalServerErrorException('LLM provider returned empty output.');
    }

    return text.trim();
  }
}
