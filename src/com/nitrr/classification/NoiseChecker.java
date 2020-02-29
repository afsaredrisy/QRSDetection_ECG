
package com.nitrr.classification;

import com.nitrr.detection.QRSDetectorParameters;


public class NoiseChecker 
	{

	private QRSDetectorParameters qrsDetParas;
	
	private int   NB_LENGTH ;
	private int[] NoiseBuffer ;
	private int   NBPtr = 0 ;
	private int   NoiseEstimate ;
	
	/**
	 * Create a new noiseChecker with the given parameters.
	 * @param bdacParameters The sampleRate-dependent parameters
	 */
	public NoiseChecker(QRSDetectorParameters qrsDetectorParameters) 
		{
		qrsDetParas = qrsDetectorParameters;
		NB_LENGTH   = qrsDetectorParameters.MS1500 ;
		NoiseBuffer = new int[NB_LENGTH] ;
		}

	/**
	 * GetNoiseEstimate() allows external access the present noise estimate.
	 * this function is only used for debugging.
	 * 
	 * @return The noise estimate
	 */
	public int GetNoiseEstimate()
		{
		return(NoiseEstimate) ;
		}

	/**
	 * NoiseCheck() must be called for every sample of data.  The data is
	 * stored in a circular buffer to facilitate noise analysis.  When a
	 * beat is detected NoiseCheck() is passed the sample delay since the
	 * R-wave of the beat occurred (delay), the RR interval between this
	 * beat and the next most recent beat, the estimated offset from the
	 * R-wave to the beginning of the beat (beatBegin), and the estimated
	 * offset from the R-wave to the end of the beat.
	 * 
	 * NoiseCheck() estimates the noise in the beat by the maximum and
	 * minimum signal values in either a window from the end of the
	 * previous beat to the beginning of the present beat, or a 250 ms
	 * window preceding the present beat, which ever is shorter.
	 * 
	 * NoiseCheck() returns ratio of the signal variation in the window
	 * between beats to the length of the window between the beats.  If
	 * the heart rate is too high and the beat durations are too long,
	 * NoiseCheck() returns 0.
	 * 
	 * @param datum
	 * @param delay
	 * @param RR
	 * @param beatBegin
	 * @param beatEnd
	 * @return ratio of the signal variation in the window between beats 
	 * to the length of the window between the beats
	 */
	public int NoiseCheck(int datum, int delay, int RR, int beatBegin, int beatEnd)
		{
		int ptr, i;
		int ncStart, ncEnd, ncMax, ncMin ;
		double noiseIndex ;

		NoiseBuffer[NBPtr] = datum ;
		if(++NBPtr == NB_LENGTH)
			NBPtr = 0 ;

		// Check for noise in region that is 300 ms following
		// last R-wave and 250 ms preceding present R-wave.

		ncStart = delay+RR-beatEnd ;	// Calculate offset to end of previous beat.
		ncEnd = delay+beatBegin ;		// Calculate offset to beginning of this beat.
		if(ncStart > ncEnd + qrsDetParas.MS250)
			ncStart = ncEnd + qrsDetParas.MS250 ;


		// Estimate noise if delay indicates a beat has been detected,
		// the delay is not to long for the data buffer, and there is
		// some space between the end of the last beat and the beginning
		// of this beat.

		if((delay != 0) && (ncStart < NB_LENGTH) && (ncStart > ncEnd))
			{

			ptr = NBPtr - ncStart ;	// Find index to end of last beat in
			if(ptr < 0)					// the circular buffer.
				ptr += NB_LENGTH ;

			// Find the maximum and minimum values in the
			// isoelectric region between beats.

			ncMax = ncMin = NoiseBuffer[ptr] ;
			for(i = 0; i < ncStart-ncEnd; ++i)
				{
				if(NoiseBuffer[ptr] > ncMax)
					ncMax = NoiseBuffer[ptr] ;
				else if(NoiseBuffer[ptr] < ncMin)
					ncMin = NoiseBuffer[ptr] ;
				if(++ptr == NB_LENGTH)
					ptr = 0 ;
				}

			// The noise index is the ratio of the signal variation
			// over the isoelectric window length, scaled by 10.

			noiseIndex = (ncMax-ncMin) ;
			noiseIndex /= (ncStart-ncEnd) ;
			NoiseEstimate = (int) (noiseIndex * 10) ;
			}
		else
			NoiseEstimate = 0 ;
		return(NoiseEstimate) ;
		}
	}
