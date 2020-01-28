package com.thesis.spectrumanalyzer;

class RecordingHandler {
    private MainActivity activity;
    private GraphicComponents graphicComponents;

    /**
     * Handling events
     * @param activity MainActivity
     * @param graphicComponents reference to view components
     */
    RecordingHandler(MainActivity activity, GraphicComponents graphicComponents) {
        this.activity = activity;
        this.graphicComponents = graphicComponents;
    }

    /**
     * Change weighting between *C* and *A*
     * @param state weighting type
     */
    void changeState(final FormulasUtil.State state){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (graphicComponents.getGraph() != null)
                    graphicComponents.getGraph().changeState(state);
            }
        });
    }

    /**
     * Send new converted data to graphic components
     * @param data converted signal
     */
    void dataChangeNotify(final long[] data){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (graphicComponents.getGraph() != null)
                    graphicComponents.getGraph().invalidate(data);
            }
        });
    }
}
