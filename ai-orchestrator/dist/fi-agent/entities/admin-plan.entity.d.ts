export declare class AdminPlan {
    id: string;
    draftInsights: string;
    confidence: number;
    requiresHumanApproval: boolean;
    status: 'DRAFT' | 'APPROVED' | 'REJECTED';
    createdAt: Date;
    reviewerComment: string;
}
