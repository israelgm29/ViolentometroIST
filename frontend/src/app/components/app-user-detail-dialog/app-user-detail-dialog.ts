import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent} from "@angular/material/dialog";
import {AppUserResponse} from "../../models/app-user";
import {MatListItem, MatListModule} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {DatePipe} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {MatDividerModule} from "@angular/material/divider";

interface InfoItem {
    label: string;
    value: string | number | null;
    icon?: string;
    color?: string;
    isBold?: boolean;
}

@Component({
    selector: 'app-app-user-detail-dialog',
    standalone: true, // Aseguramos que sea standalone para Angular 20
    imports: [
        MatDialogContent,
        MatDividerModule,
        MatIcon,
        MatListModule,
        MatListItem,
        MatDialogActions,
        MatButtonModule,
        MatDialogClose
    ],
    providers: [DatePipe],
    templateUrl: './app-user-detail-dialog.html',
    styleUrl: './app-user-detail-dialog.css',
})
export class AppUserDetailDialog implements OnInit {

    personalInfo: InfoItem[] = [];
    institutionalInfo: InfoItem[] = [];

    constructor(@Inject(MAT_DIALOG_DATA) public appUser: AppUserResponse, private datePipe: DatePipe) {}

    ngOnInit() {
        this.loadDataArrays();
    }

    private loadDataArrays() {
        this.personalInfo = [
            { label: 'Cédula', value: this.appUser.dni, color: '#3f51b5', isBold: true },
            { label: 'Género', value: this.appUser.gender.name || '---' },
            { label: 'Fecha de Nacimiento', value: this.datePipe.transform(this.appUser.birthdate, 'dd/MM/yyyy') },
            { label: 'Edad', value: `${this.getAge()} años` },

        ];

        this.institutionalInfo = [
            { label: 'Instituto', value: this.appUser.institute.name, color: '#1976d2', isBold: true },
            { label: 'Región', value: this.appUser.region.name || '---' },
            { label: 'Discapacidad', value: this.appUser.disability.name || 'Ninguna' },
            { label: 'Etnia', value: this.appUser.ethnicity.name || 'No especificado' }
        ];
    }

    getAge(): number {
        if (!this.appUser.birthdate) return 0;
        const today = new Date();
        const birthDate = new Date(this.appUser.birthdate);
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) age--;
        return age;
    }

    getInitials(): string {
        return this.appUser.dni ? this.appUser.dni.substring(0, 2).toUpperCase() : 'U';
    }

    getStatusColor(): string {
        return this.appUser.status ? '#2e7d32' : '#c62828';
    }

    getStatusText(): string {
        return this.appUser.status ? 'Usuario Activo' : 'Usuario Inactivo';
    }
}