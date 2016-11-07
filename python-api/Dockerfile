FROM python:2.7

# Create working folder
RUN mkdir /home/app
WORKDIR /home/app

# Copy in our requirements and install via pip
COPY requirements.txt /home/app
RUN pip install -r requirements.txt
