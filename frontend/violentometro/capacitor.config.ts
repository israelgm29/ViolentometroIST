import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.violentometro.app',
  appName: 'Violentómetro',
  webDir: 'www/browser',  // RUTA CORRECTA
  server: {
    androidScheme: 'https'
  }
};

export default config;
