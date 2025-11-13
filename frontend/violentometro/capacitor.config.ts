import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.violentometro.app',
  appName: 'Violentómetro',
  webDir: 'www/browser',
  server: {
    androidScheme: 'https'
  }
};

export default config;
