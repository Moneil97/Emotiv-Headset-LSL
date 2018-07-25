classdef EEGImporterLSL < matlab.System
    % Untitled Add summary here
    %
    % This template includes the minimum set of functions required
    % to define a System object with discrete state.
    
    % Public, tunable properties
    properties
        
    end
    
    properties (Hidden)%(DiscreteState)
        
    end
    
    % Pre-computed constants
    properties(Access = private)
        
    end
    
    methods(Access = protected)
        function setupImpl(obj)

        end
        
        function y = stepImpl(obj)
            
            coder.extrinsic('evalin');
            y = zeros(18,1);
            y = evalin('base', 'transpose(inlet.pull_sample())');

        end
        
        function resetImpl(obj)
            % Initialize / reset discrete-state properties
        end
    end
end