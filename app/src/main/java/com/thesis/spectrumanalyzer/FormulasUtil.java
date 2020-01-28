package com.thesis.spectrumanalyzer;

class FormulasUtil {

    static final int _SAMPLING_RATE_ = 44100;
    static final int _AUDIO_RECORD_MAX_VALUE_ = 32768;
    static final int _FFT_POINTS_ = 1024;
    static final int _CALIBRATION_ = 20;
    static final int _PERIOD_NUMBER_ = 33;
    private static final double _BASIC_FREQUENCY_ = 12.5;

    /**
     * selected weighting
     */
    enum State { A_WEIGHTING , C_WEIGHTING }

    /**
     * source: https://docplayer.pl/56050559-Widma-tercjowe-i-oktawowe-poziomu-cisnienia-dzwieku-bez-i-z-zastosowaniem-filtra-korekcyjnego-a-w-pasmie-slyszalnym.html
     * @param numberOfTerce thirds serial number
     * @return cutoff frequency
     */
    static double cutoffFrequency(int numberOfTerce) {
        return _BASIC_FREQUENCY_ * Math.pow(2d , (double)(2 * numberOfTerce - 1) / 6);
    }

    /**
     * source: https://docplayer.pl/56050559-Widma-tercjowe-i-oktawowe-poziomu-cisnienia-dzwieku-bez-i-z-zastosowaniem-filtra-korekcyjnego-a-w-pasmie-slyszalnym.html
     * @param numberOfTerce thirds serial number
     * @return middle frequency
     */
    private static double middleFrequency(int numberOfTerce) {
        return _BASIC_FREQUENCY_ * Math.pow(2d , (double)(numberOfTerce - 1) / 3);
    }

    /**
     * A-weighting formula
     * source: http://www.sengpielaudio.com/BerechnungDerBewertungsfilter.pdf
     * @param numberOfTerce thirds serial number
     * @return damping
     */
    static int weightingA(int numberOfTerce) {
        double f = middleFrequency(numberOfTerce);
        double Ra = (Math.pow(12200 , 2) * Math.pow(f , 4)) /
                ((Math.pow(f , 2) + Math.pow(20.6 , 2)) * (Math.pow(f , 2) + Math.pow(12200 , 2)) *
                        Math.sqrt(Math.pow(f , 2) + Math.pow(107.7 , 2)) *
                        Math.sqrt(Math.pow(f , 2) + Math.pow(737.9 , 2)));
        return (int)(20 * Math.log10(Ra) + 2);
    }

    /**
     * C-weighting formula
     * source: http://www.sengpielaudio.com/BerechnungDerBewertungsfilter.pdf
     * @param numberOfTerce thirds serial number
     * @return damping
     */
    static int weightingC(int numberOfTerce) {
        double f = middleFrequency(numberOfTerce);
        double Rc = (Math.pow(12200 , 2) * Math.pow(f , 2)) /
                ((Math.pow(f , 2) + Math.pow(20.6 , 2)) * (Math.pow(f , 2) + Math.pow(12200 , 2)));
        return (int)(20 * Math.log10(Rc) + 0.06);
    }

}
