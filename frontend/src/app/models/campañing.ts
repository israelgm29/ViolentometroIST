export interface CampaignCategory {
    id:     number;
    name:   string;
    color:  string;
    icon:   string;
    status: boolean;
}

export interface CampaignDTO {
    id:          number;
    title:        string;
    excerpt?:     string;
    imageUrl?:    string;
    externalUrl?: string;
    featured:     boolean;
    status:       boolean;
    publishDate?: string;
    startDate?:   string;
    endDate?:     string;
    categoryId:   number;
    categoryName?:  string;
    categoryColor?: string;
    categoryIcon?:  string;
    createdAt?:   string;
}