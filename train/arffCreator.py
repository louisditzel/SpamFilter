import os

quoteMark = '"'

with open("output.arff", "w") as outputFile:
	outputFile.write("@relation hamOrSpam\n")
	outputFile.write("@attribute email string\n")
	outputFile.write("@attribute hamOrSpam {ham, spam}\n")
	outputFile.write("@data\n\n")
	for i in os.listdir(os.getcwd()):
		if i.endswith(".txt"):
			##print(i)
			with open(i, "r") as f:
				try:
					read_data = f.read().replace("\n", " ").replace('"', "'")
				except:
					print(i)
					read_data = ""
					for line in f:
						print(line)
				outputFile.write(quoteMark)
				outputFile.write(read_data)
				outputFile.write(quoteMark)
				outputFile.write(", ")
				if "ham" in i:
					outputFile.write("ham")
				else:
					outputFile.write("spam")
				outputFile.write("\n")
