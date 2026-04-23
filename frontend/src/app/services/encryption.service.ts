import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class EncryptionService {

    private readonly SECRET_KEY = 'MiClaveSecreta12345678901234AB55';
    // IV fijo de 16 bytes — mismo en backend y frontend
    private readonly IV = new Uint8Array(16);

    async encrypt(data: string): Promise<string> {
        const encoder    = new TextEncoder();
        const keyData    = encoder.encode(this.SECRET_KEY);

        const cryptoKey = await crypto.subtle.importKey(
            'raw', keyData,
            { name: 'AES-CBC' },
            false,
            ['encrypt']
        );

        const encrypted = await crypto.subtle.encrypt(
            { name: 'AES-CBC', iv: this.IV },
            cryptoKey,
            encoder.encode(data)
        );

        return btoa(String.fromCharCode(...new Uint8Array(encrypted)))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=/g, '');
    }
}