
package com.nitrr.classification;

import com.nitrr.classification.Classifier.ClassifyResult;
import com.nitrr.detection.QRSDetector2;
import com.nitrr.detection.QRSDetectorParameters;


public class BeatDetectionAndClassification 
	{

	private BDACParameters bdacParas ;
	private QRSDetectorParameters qrsDetParas ;
	private QRSDetector2 qrsDetector ;
	private NoiseChecker noiseChecker ;
	private Matcher matcher ;
	private Classifier classifier ;
	
	public final int ECG_BUFFER_LENGTH = 2000 ;	// Should be long enough for a beat
	// plus extra space to accommodate the maximum detection delay.

	public final int BEAT_QUE_LENGTH = 10 ; // Length of que for beats awaiting
	// classification.  Because of detection delays, Multiple beats
	// can occur before there is enough data to classify the first beat in the que.
	
	public int[] ECGBuffer = new int[ECG_BUFFER_LENGTH] ;
	public int ECGBufferIndex = 0 ;  // Circular data buffer.
	public int[] BeatBuffer ;
	public int[] BeatQue = new int[BEAT_QUE_LENGTH];
	public int BeatQueCount = 0 ;  // Buffer of detection delays.
	public int RRCount = 0 ;
	public int InitBeatFlag = 1 ;
	

	public BeatDetectionAndClassification(BDACParameters bdacParameters, 
			QRSDetectorParameters qrsDetectorParameters) 
		{
		bdacParas = bdacParameters;
		qrsDetParas = qrsDetectorParameters ;
		BeatBuffer = new int[bdacParas.BEATLGTH] ;
		}

	public void setObjects(QRSDetector2 qrsDetector, NoiseChecker noiseChecker, 
		Matcher matcher, Classifier classifier) 
		{
		this.qrsDetector = qrsDetector ;
		this.noiseChecker = noiseChecker ;
		this.matcher = matcher ;
		this.classifier = classifier ;
		}
	

	public BeatDetectAndClassifyResult BeatDetectAndClassify(int ecgSample)
		{
		BeatDetectAndClassifyResult result = new BeatDetectAndClassifyResult();
		int detectDelay, rr = 0, i, j ;
		int noiseEst = 0, beatBegin = 0, beatEnd = 0 ;
		int domType ;
		int fidAdj ;
		int[] tempBeat = new int[(qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE)*bdacParas.BEATLGTH] ;
	
		// Store new sample in the circular buffer.
	
		ECGBuffer[ECGBufferIndex] = ecgSample ;
		if(++ECGBufferIndex == ECG_BUFFER_LENGTH)
			ECGBufferIndex = 0 ;
	
		// Increment RRInterval count.
	
		++RRCount ;
	
		// Increment detection delays for any beats in the que.
	
		for(i = 0; i < BeatQueCount; ++i)
			++BeatQue[i] ;
	
		// Run the sample through the QRS detector.
	
		detectDelay = qrsDetector.QRSDet(ecgSample) ;
		if(detectDelay != 0)
			{
			BeatQue[BeatQueCount] = detectDelay ;
			++BeatQueCount ;
			}
	
		// Return if no beat is ready for classification.
	
		if((BeatQue[0] < (bdacParas.BEATLGTH-bdacParas.FIDMARK)*(qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE))
			|| (BeatQueCount == 0))
			{
			noiseChecker.NoiseCheck(ecgSample,0,rr, beatBegin, beatEnd) ;	// Update noise check buffer
			result.samplesSinceRWaveIfSuccess = 0;
			return result ;
			}
	
		// Otherwise classify the beat at the head of the que.
	
		rr = RRCount - BeatQue[0] ;	// Calculate the R-to-R interval
		detectDelay = RRCount = BeatQue[0] ;
	
		// Estimate low frequency noise in the beat.
		// Might want to move this into classify().
	
		domType = matcher.GetDominantType() ;
		if(domType == -1)
			{
			beatBegin = qrsDetParas.MS250 ;
			beatEnd = qrsDetParas.MS300 ;
			}
		else
			{
			beatBegin = (qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE)*(bdacParas.FIDMARK-matcher.GetBeatBegin(domType)) ;
			beatEnd = (qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE)*(matcher.GetBeatEnd(domType)-bdacParas.FIDMARK) ;
			}
		noiseEst = noiseChecker.NoiseCheck(ecgSample,detectDelay,rr,beatBegin,beatEnd) ;
	
		// Copy the beat from the circular buffer to the beat buffer
		// and reduce the sample rate by averageing pairs of data
		// points.
	
		j = ECGBufferIndex - detectDelay - (qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE)*bdacParas.FIDMARK ;
		if(j < 0) j += ECG_BUFFER_LENGTH ;
	
		for(i = 0; i < (qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE)*bdacParas.BEATLGTH; ++i)
			{
			tempBeat[i] = ECGBuffer[j] ;
			if(++j == ECG_BUFFER_LENGTH)
				j = 0 ;
			}
	
		DownSampleBeat(BeatBuffer,tempBeat) ;
	
		// Update the QUE.
	
		for(i = 0; i < BeatQueCount-1; ++i)
			BeatQue[i] = BeatQue[i+1] ;
		--BeatQueCount ;
	
	
		// Skip the first beat.
	
		if(InitBeatFlag != 0)
			{
			InitBeatFlag = 0 ;
			result.beatType = 13 ;
			result.beatMatch = 0 ;
			fidAdj = 0 ;
			}
	
		// Classify all other beats.
	
		else
			{
			ClassifyResult cr = classifier.Classify(BeatBuffer,rr,noiseEst);
			result.beatMatch = cr.beatMatch;
			result.beatType = cr.tempClass;
			fidAdj = cr.fidAdj;
			fidAdj *= qrsDetParas.SAMPLE_RATE/bdacParas.BEAT_SAMPLE_RATE ;
	      }
	
		// Ignore detection if the classifier decides that this
		// was the trailing edge of a PVC.
	
		if(result.beatType == 100)
			{
			RRCount += rr ;
			result.samplesSinceRWaveIfSuccess = 0;
			return(result) ;
			}
	
		// Limit the fiducial mark adjustment in case of problems with
		// beat onset and offset estimation.
	
		if(fidAdj > qrsDetParas.MS80)
			fidAdj = qrsDetParas.MS80 ;
		else if(fidAdj < -qrsDetParas.MS80)
			fidAdj = -qrsDetParas.MS80 ;
	
		result.samplesSinceRWaveIfSuccess = detectDelay-fidAdj;
		return(result) ;
		}
	
	/**
	 * The result of a beat detection and classification
	 */
	public class BeatDetectAndClassifyResult 
		{
		/** If a beat was classified this field contains the samples since the R-Wave of the beat */
		public int samplesSinceRWaveIfSuccess;
		public int beatType;
		public int beatMatch;
		}
	
	private void DownSampleBeat(int[] beatOut, int[] beatIn)
		{
		int i ;
	
		for(i = 0; i < bdacParas.BEATLGTH; ++i)
			beatOut[i] = (beatIn[i<<1]+beatIn[(i<<1)+1])>>1 ;
		}
	}
