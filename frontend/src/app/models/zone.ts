export interface ViolenceZoneInterface {
    id: number;
    name: string;
    description: string;
    color: string;
    severity: number;
    status: boolean;
    // Campos dinámicos configurados por el admin
    resultTitle?: string;
    resultMessage?: string;
    recommendations?: string[];
}