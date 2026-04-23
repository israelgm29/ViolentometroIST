import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {QuestionZone} from "../models/question-zone";
import {Question} from "../models/question";

@Injectable({
  providedIn: 'root',
})
export class QuestionsService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/questions`;
  private apiUrlInclude = `${environment.apiUrl}/v1/questions?include=zone`;



  constructor() { }

  getQuestions(): Observable<Question[]> {
    return this.http.get<Question[]>(this.apiUrl);
  }
  getQuestionsWithZone(): Observable<QuestionZone[]> {
    return this.http.get<QuestionZone[]>(this.apiUrlInclude);
  }

    createQuestion(question: Question): Observable<Question> {
    return this.http.post<Question>(this.apiUrl, question);
    }

    updateQuestion(id: number, question: Question): Observable<Question> {
    return this.http.put<Question>(`${this.apiUrl}/${id}`, question);
    }

    deleteQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }


}
