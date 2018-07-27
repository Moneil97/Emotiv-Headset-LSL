#include "../../../include/lsl_c.h"
#include <stdio.h>

/**
* Example program that demonstrates how to resolve a specific stream on the lab network and how to connect to it in order to receive data.
*/

#define CHANNELS 18

int main(int argc, char* argv[]) {

	unsigned k;					/* channel index */
	lsl_streaminfo info;		/* the streaminfo returned by the resolve call */
	lsl_inlet inlet;			/* a stream inlet to get samples from */
	int errcode;				/* error code (lsl_lost_error or timeouts) */
	float cursample[CHANNELS];	/* array to hold our current sample */
	double timestamp;			/* time stamp of the current sample (in sender time) */

	/* resolve the stream of interest (result array: info, array capacity: 1 element, type shall be EEG, resolve at least 1 stream, wait forever if necessary) */
	printf("Now waiting for an EEG stream...\n");
	lsl_resolve_byprop(&info,1, "type","EEG", 1, LSL_FOREVER);

	/* make an inlet to read data from the stream (buffer max. 300 seconds of data, no preference regarding chunking, automatic recovery enabled) */
	inlet = lsl_create_inlet(info, 300, LSL_NO_PREFERENCE, 1);

	/* subscribe to the stream (automatically done by push, but a nice way of checking early on that we can connect successfully) */
	lsl_open_stream(inlet,LSL_FOREVER,&errcode);
	if (errcode != 0)
		return errcode;

	printf("Displaying data...\n");
	while(1) {
		/* get the next sample form the inlet (read into cursample, 18 values, wait forever if necessary) and return the timestamp if we got something */
		timestamp = lsl_pull_sample_f(inlet, cursample, CHANNELS,LSL_FOREVER ,&errcode);

		/* print the data */
		for (k=0; k<CHANNELS; ++k)
			printf("\t%.2f",cursample[k]);
		printf("\n");
	}

	/* we never get here, but anyway */
	lsl_destroy_inlet(inlet);

	return 0;
}
