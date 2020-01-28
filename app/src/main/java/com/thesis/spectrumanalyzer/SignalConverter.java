package com.thesis.spectrumanalyzer;

import com.thesis.mylibrary.fft.Complex;
import com.thesis.mylibrary.fft.FFT;

class SignalConverter {

    private Complex[] data;

    /**
     * Converting raw data from AudioRecord to complex value array
     * @param rawData AudioRecord time signal data
     */
    void convertToComplex(byte[] rawData){
        double temp;
        data = new Complex[FormulasUtil._FFT_POINTS_];
        for (int i = 0 ; i<FormulasUtil._FFT_POINTS_; i++) {
            temp = (double)((rawData[2*i] & 0xFF) | (rawData[2*i+1] << 8)) / FormulasUtil._AUDIO_RECORD_MAX_VALUE_;
            data[i] = new Complex(temp * FormulasUtil._CALIBRATION_, 0);
        }
    }

    /**
     * Fast Fourier transform of complex data
     */
    void fft(){
        data = FFT.fft(data);
    }

    /**
     * Dividing signal in the frequency domain to 33 thirds
     * @return array of real and imaginary values of divided signal samples
     */
    long[] divideToThirds() {
        final long[] terceArray = new long[FormulasUtil._PERIOD_NUMBER_ * 2];
        for (int numberOfTerce = 1, iterator = 0 ; iterator < data.length ; iterator++){
            if((double)(iterator*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_POINTS_) > FormulasUtil.cutoffFrequency(33))
                break;
            if((double)(iterator*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_POINTS_) > FormulasUtil.cutoffFrequency(numberOfTerce)
                    && (double)(iterator*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_POINTS_) < (double)FormulasUtil._SAMPLING_RATE_/2)
                numberOfTerce++;
            if(data[iterator].re() > terceArray[2 * numberOfTerce - 2]) {
                terceArray[2 * numberOfTerce - 2] = (long) data[iterator].re();
                terceArray[2 * numberOfTerce - 1] = (long) data[iterator].im();
            }
        }
        return terceArray;
    }
}
