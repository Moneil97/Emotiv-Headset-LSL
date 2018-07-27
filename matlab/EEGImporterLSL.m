classdef EEGImporterLSL < matlab.System
    % Gets 18 EEG Channels from Lab Streaming Layer
    
    methods(Access = protected)
        function setupImpl(~)
            coder.extrinsic('evalin')
            coder.extrinsic('assignin')
            assignin('base', 'lib', evalin('base', 'lsl_loadlib()'));

            %Wait for an available EEG Stream, then get all EEG Streams
            while isempty(evalin('base', "lsl_resolve_byprop(lib,'type','EEG')"))
            end
            assignin('base', 'result', evalin('base', "lsl_resolve_byprop(lib,'type','EEG')"));
            
            %Create inlet on first available EEG Stream
            assignin('base', 'inlet', evalin('base', "lsl_inlet(result{1})"));
        end
        
        function y = stepImpl(~)
            coder.extrinsic('evalin');
            y = zeros(18,1);
            y = evalin('base', 'transpose(inlet.pull_sample())');
        end
        
        function resetImpl(~)
        end
    end
end