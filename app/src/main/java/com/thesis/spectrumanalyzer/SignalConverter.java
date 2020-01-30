package com.thesis.spectrumanalyzer;

import com.thesis.mylibrary.fft.Complex;
import com.thesis.mylibrary.fft.FFT;

class SignalConverter {

    private long[] amplitudeData;
    private Complex[] complexData;

    /**
     * Converting raw data from AudioRecord to complex value array
     * @param rawData AudioRecord time signal data
     */
    void convertToComplex(byte[] rawData){
        double temp;
        complexData = new Complex[FormulasUtil._FFT_SIZE_];
        for (int iterator = 0 ; iterator < FormulasUtil._FFT_SIZE_ ; iterator++) {
            temp = (double)((rawData[2 * iterator] & 0xFF) | (rawData[2 * iterator + 1] << 8)) / FormulasUtil._AUDIO_RECORD_MAX_VALUE_;
            complexData[iterator] = new Complex(temp * FormulasUtil._CALIBRATION_, 0);
        }
    }

    /**
     * Fast Fourier transform of complex data
     */
    void fft(){
        complexData = FFT.fft(complexData);
    }

    /**
     * Dividing signal in the frequency domain to 33 thirds
     */
    void divideToThirds() {
        amplitudeData = new long[FormulasUtil._THIRDS_NUMBER_];
        for (int numberOfTerce = 1, iterator = 0 ; iterator < complexData.length ; iterator++){
            if((double)(iterator*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_SIZE_) > FormulasUtil.cutoffFrequency(33))
                break;
            if((double)((iterator+1)*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_SIZE_) > FormulasUtil.cutoffFrequency(numberOfTerce)
                    && (double)(iterator*FormulasUtil._SAMPLING_RATE_/FormulasUtil._FFT_SIZE_) < (double)FormulasUtil._SAMPLING_RATE_/2){
                numberOfTerce++;
                iterator--;
                continue;
            }
            if((Math.pow(complexData[iterator].re(),2) + Math.pow(complexData[iterator].im(),2)) > amplitudeData[numberOfTerce - 1])
                amplitudeData[numberOfTerce - 1] = (long) (Math.pow(complexData[iterator].re(),2) + Math.pow(complexData[iterator].im(),2));
        }
    }

    /**
     * Filtering and converting signal to decibels value and
     * @param state activity selected state
     * @return converted data
     */
    int[] getDbSignalForm(FormulasUtil.State state){
        int[] newData = new int[FormulasUtil._THIRDS_NUMBER_];
        for(int iterator = 0 ; iterator < newData.length ; iterator++)
            newData[iterator] = Math.max(0, getDamping(iterator, state) + FormulasUtil.convertToDecibels(amplitudeData[iterator]));
        return newData;
    }

    /**
     * Changing selected weighting depends on activity state
     * @param numberOfTerce thirds serial number
     * @param state selected state
     * @return damping value in decibels
     */
    private int getDamping(int numberOfTerce, FormulasUtil.State state){
        switch (state) {
            case A_WEIGHTING:
                return FormulasUtil.weightingA(numberOfTerce);
            case C_WEIGHTING:
                return FormulasUtil.weightingC(numberOfTerce);
            default:
                return 0;
        }
    }
}
