import { Injectable } from '@angular/core';
import { CatalogService } from './catalog.service';
import { MasterCatalog } from '../models/app-user';
import { lastValueFrom } from 'rxjs';
import * as XLSX from 'xlsx';

@Injectable({
  providedIn: 'root'
})
export class ExcelTemplateService {
  constructor(private catalogService: CatalogService) {}

  async downloadTemplate(): Promise<void> {
    try {
      // Obtener todos los catálogos
      const [genders, regions, ethnicities, disabilities, institutes] = await Promise.all([
        lastValueFrom(this.catalogService.findAll('genders')),
        lastValueFrom(this.catalogService.findAll('regions')),
        lastValueFrom(this.catalogService.findAll('ethnicities')),
        lastValueFrom(this.catalogService.findAll('disabilities')),
        lastValueFrom(this.catalogService.findAll('institutes'))
      ]);

      // Crear hoja de datos
      const dataSheet = this.createDataSheet();
      
      // Crear hoja de referencia
      const referenceSheet = this.createReferenceSheet(genders || [], regions || [], ethnicities || [], disabilities || [], institutes || []);

      // Crear workbook
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, dataSheet, 'Datos');
      XLSX.utils.book_append_sheet(wb, referenceSheet, 'Referencia');

      // Generar y descargar archivo
      XLSX.writeFile(wb, 'plantilla_carga_masiva.xlsx');
    } catch (error) {
      console.error('Error al generar plantilla:', error);
      throw error;
    }
  }

  private createDataSheet(): XLSX.WorkSheet {
    // Encabezados de la hoja de datos (según backend)
    const headers = [
      'Cédula',
      'Género',
      'Fecha de nacimiento (dd/MM/yyyy)',
      'Provincia',
      'Etnia',
      'Código Instituto',
      'Discapacidad (opcional)'
    ];

    // Ejemplo de fila
    const exampleRow = [
      '0101010101',
      'Masculino',
      '01/01/2000',
      'Pichincha',
      'Mestizo',
      'INST001',
      'Ninguna'
    ];

    const data = [headers, exampleRow];
    return XLSX.utils.aoa_to_sheet(data);
  }

  private createReferenceSheet(
    genders: MasterCatalog[],
    regions: MasterCatalog[],
    ethnicities: MasterCatalog[],
    disabilities: MasterCatalog[],
    institutes: MasterCatalog[]
  ): XLSX.WorkSheet {
    const data: any[] = [];

    // Título
    data.push(['TABLA DE REFERENCIA - VALORES VÁLIDOS']);
    data.push([]);

    // Géneros
    data.push(['GÉNEROS']);
    data.push(['Nombre']);
    genders.forEach(g => data.push([g.name]));
    data.push([]);

    // Regiones (Provincias)
    data.push(['PROVINCIAS']);
    data.push(['Nombre']);
    regions.forEach(r => data.push([r.name]));
    data.push([]);

    // Autoidentificación étnica
    data.push(['ETNIAS']);
    data.push(['Nombre']);
    ethnicities.forEach(e => data.push([e.name]));
    data.push([]);

    // Discapacidades
    data.push(['DISCAPACIDADES']);
    data.push(['Nombre']);
    disabilities.forEach(d => data.push([d.name]));
    data.push([]);

    // Nota sobre discapacidad opcional
    data.push(['Nota: Si el estudiante no tiene discapacidad, use "Ninguna", "N/A" o deje vacío']);
    data.push([]);

    // Institutos
    data.push(['INSTITUTOS']);
    data.push(['Código', 'Nombre']);
    institutes.forEach(i => data.push([i.code || i.id, i.name]));
    data.push([]);

    // Nota sobre código de instituto
    data.push(['Nota: Use el CÓDIGO del instituto en la columna "Código Instituto" de la hoja de Datos']);

    return XLSX.utils.aoa_to_sheet(data);
  }
}
