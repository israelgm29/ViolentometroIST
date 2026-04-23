export interface UserAnswerRequest {
    idAppUser: number;
    idQuestion: number;
    answer: boolean;
}

export interface UserAnswerResponse {
    id: number;
    userId: number;
    questionId: number;
    answer: boolean;
    createdAt: string;
}

export interface UserAnswerDTO {
    idAnswer:   number;
    answer:     boolean;
    idAppUser:  number;
    idQuestion: number;
}