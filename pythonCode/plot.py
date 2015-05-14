# -*- coding: utf-8 -*-
"""
Created on Fri Mar 20 11:05:16 2015

@author: vmartin
"""

import csv
import numpy as np
import matplotlib.pyplot as plt
from pylab import *

values = []
magnitude = []

with open('360.csv') as csvfile:
    filereader = csv.reader(csvfile, delimiter=',')
    for row in filereader:
        values.append(row)


for i in range(len(values)):
    tempX = np.power(float(values[i][4]),2)
    tempY = np.power(float(values[i][5]),2)
    tempZ = np.power(float(values[i][6]),2)
    magnitude.append(np.sqrt(tempX + tempY + tempZ))



r = 5
X = 0
Y = 0
ax = plt.gca()
ax.set_xlim([-15,15])
ax.set_ylim([-15,15])

for i in range(len(values)):
    if values[i][1] < 0:
        values[i][1] = values[i][1] + 2*pi


for i in range(len(values)):
        
    
    U = 10*np.cos(float(values[i][1]))
    V = 10*np.sin(float(values[i][1]))
    Ux = U - X
    Vx = V - Y
    
    
    """
    ax.arrow(X,Y,Ux,Vx, head_width=0.05, head_length=0.1, fc='k', ec='k')
    """
    
    plt.draw()
    ax.quiver(0,0,U,V,angles='xy',scale_units='xy',scale=1)    

    
    print "X",X
    print "Y",Y
    print "U",U
    print "V",V    
    X = U
    Y = V
    
plt.draw()
plt.savefig('temp.png')
